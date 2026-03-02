package com.example.teene.authentication.register.trainer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.UserDataStore
import com.example.teene.home.data.repositories.ImagesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Manages picking, uploading and removing trainer images during trainer registration.
 */
class RegisterTrainerImagesViewModel(
    private val imagesRepository: ImagesRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    data class ImageUi(
        val localUri: Uri? = null,
        val remoteId: Int? = null,
        val remoteUrl: String? = null,
        val status: Status = Status.Idle,
        val error: String? = null,
    ) {
        enum class Status { Idle, Uploading, Uploaded, Error }
    }

    private val _images = MutableStateFlow<List<ImageUi>>(emptyList())
    val images: StateFlow<List<ImageUi>> = _images.asStateFlow()

    private fun canAddMore(newCount: Int = 1): Boolean = _images.value.count() + newCount <= 4

    // Step 1: Only pick and stage images locally (no upload yet)
    fun pick(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val remainingSlots = 4 - _images.value.size
        val toTake = uris.take(remainingSlots)
        if (toTake.isEmpty()) return
        val staged = toTake.map { ImageUi(localUri = it, status = ImageUi.Status.Idle) }
        _images.value = _images.value + staged
    }

    // Step 2: Upload all staged (not yet uploaded) images when user taps Next
    suspend fun uploadAllAndReturnResult(): Boolean {
        val trainerId = userDataStore.userIdFlow.first() ?: return false
        var allSuccess = true
        // We iterate by index to allow in-place updates
        val current = _images.value
        current.forEachIndexed { index, item ->
            if (item.remoteId == null && item.localUri != null) {
                // mark as uploading
                val listUploading = _images.value.toMutableList()
                if (index in listUploading.indices) {
                    listUploading[index] = listUploading[index].copy(status = ImageUi.Status.Uploading, error = null)
                    _images.value = listUploading
                }
                val result = runCatching { imagesRepository.uploadTrainerImage(trainerId, item.localUri).getOrThrow() }
                if (result.isSuccess) {
                    val uploaded = result.getOrNull()
                    val list = _images.value.toMutableList()
                    if (index in list.indices && uploaded != null) {
                        list[index] = list[index].copy(
                            remoteId = uploaded.id,
                            remoteUrl = uploaded.image?.url,
                            status = ImageUi.Status.Uploaded,
                            error = null
                        )
                        _images.value = list
                    }
                } else {
                    allSuccess = false
                    val list = _images.value.toMutableList()
                    if (index in list.indices) {
                        list[index] = list[index].copy(status = ImageUi.Status.Error, error = result.exceptionOrNull()?.message)
                        _images.value = list
                    }
                }
            }
        }
        return allSuccess
    }

    fun retryUpload(index: Int) {
        val item = _images.value.getOrNull(index) ?: return
        val uri = item.localUri ?: return
        if (item.status != ImageUi.Status.Error) return
        val list = _images.value.toMutableList()
        list[index] = item.copy(status = ImageUi.Status.Uploading, error = null)
        _images.value = list
        viewModelScope.launch {
            val trainerId = userDataStore.userIdFlow.first() ?: return@launch
            runCatching { imagesRepository.uploadTrainerImage(trainerId, uri).getOrThrow() }
                .onSuccess { uploaded ->
                    val updated = _images.value.toMutableList()
                    updated[index] = _images.value[index].copy(
                        remoteId = uploaded.id,
                        remoteUrl = uploaded.image?.url,
                        status = ImageUi.Status.Uploaded,
                        error = null
                    )
                    _images.value = updated
                }
                .onFailure { e ->
                    val updated = _images.value.toMutableList()
                    updated[index] = _images.value[index].copy(status = ImageUi.Status.Error, error = e.message)
                    _images.value = updated
                }
        }
    }

    fun removeAt(index: Int) {
        val item = _images.value.getOrNull(index) ?: return
        // If already uploaded, call delete first
        val remoteId = item.remoteId
        if (remoteId == null) {
            // Just remove locally
            _images.value = _images.value.toMutableList().also { if (index in it.indices) it.removeAt(index) }
            return
        }
        viewModelScope.launch {
            val result = imagesRepository.deleteImage(remoteId)
            if (result.isSuccess) {
                _images.value = _images.value.toMutableList().also { if (index in it.indices) it.removeAt(index) }
            } else {
                // mark error state so user can retry removal if needed (keeping for now)
                val list = _images.value.toMutableList()
                list[index] = item.copy(status = ImageUi.Status.Error, error = result.exceptionOrNull()?.message)
                _images.value = list
            }
        }
    }
}
