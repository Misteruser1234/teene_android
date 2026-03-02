package com.example.teene.home.data.models

import com.google.gson.annotations.SerializedName

/**
 * DTOs for generic Images API (POST /images, DELETE /images/{id}).
 * Matches the trainer images structure used elsewhere: images -> image -> url
 */

data class UploadedImageDto(
    val id: Int,
    val image: ImageDto?
)

/**
 * Request parts for multipart upload when not using @PartMap.
 * Provided here only for reference; Retrofit will use primitive parts directly.
 */
data class CreateImageRequest(
    @SerializedName("imageable_id") val imageableId: Int,
    @SerializedName("imageable_type") val imageableType: String,
)
