package com.example.teene.home.data.repositories

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.UploadedImageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

interface ImagesRepository {
    suspend fun uploadTrainerImage(trainerId: Int, uri: Uri): Result<UploadedImageDto>
    suspend fun deleteImage(imageId: Int): Result<Unit>
}

class ImagesRepositoryImpl(
    private val api: AuthorizedApiService,
    private val contentResolver: ContentResolver,
) : ImagesRepository {

    override suspend fun uploadTrainerImage(trainerId: Int, uri: Uri): Result<UploadedImageDto> = withContext(Dispatchers.IO) {
        try {
            val mime = contentResolver.getType(uri) ?: "image/*"
            val fileName = queryFileName(uri) ?: "upload_${System.currentTimeMillis()}"
            val tempFile = createTempFile(prefix = "img_", suffix = "_${fileName}")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(IllegalStateException("Unable to open input stream for URI: $uri"))

            val imageRequestBody = tempFile.asRequestBody(mime.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                name = "image",
                filename = fileName,
                body = imageRequestBody
            )

            val idPart: RequestBody = trainerId.toString().toRequestBody(MultipartBody.FORM)
            val typePart: RequestBody = "Trainer".toRequestBody(MultipartBody.FORM)

            val resp = api.uploadImage(
                imageableId = idPart,
                imageableType = typePart,
                image = imagePart
            )
            tempFile.delete()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) Result.success(body) else Result.failure(IllegalStateException("Empty body"))
            } else {
                Result.failure(IllegalStateException("Upload failed: ${resp.code()} ${resp.message()}"))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun deleteImage(imageId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.deleteImage(imageId)
            if (resp.isSuccessful) Result.success(Unit) else Result.failure(IllegalStateException("Delete failed: ${resp.code()} ${resp.message()}"))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun queryFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) return it.getString(idx)
            }
        }
        return null
    }
}
