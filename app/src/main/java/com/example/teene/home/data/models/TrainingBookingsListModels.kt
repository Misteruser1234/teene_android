package com.example.teene.home.data.models

import com.google.gson.annotations.SerializedName

/**
 * DTOs for GET /training_bookings response.
 * We only model fields we actually need — unknown fields are ignored by Gson.
 */

data class TrainingBookingDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String?,
    val approved: Boolean?,
    @SerializedName("cancelled_at") val cancelledAt: String?,
    @SerializedName("created_at") val createdAt: String?,
    val trainer: TrainerSummaryDto?,
    val user: UserSummaryDto? = null
)

data class UserSummaryDto(
    val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)

data class TrainerSummaryDto(
    val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val rate: Double?,
    val currency: String?,
    val address: String?,
    val rating: Double?,
    @SerializedName("review_count") val reviewCount: Int?,
    val about: String?,
    val images: List<TrainerImageDto>?,
    val sport: SportDto?,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)

data class SportDto(
    val id: Int,
    val name: String?,
    val image: ImageDto?
)

data class ImageDto(
    val url: String?
)

// New wrapper to match API: trainer -> images -> image -> url
// Example JSON snippet:
// "images": [ { "image": { "url": "https://..." } } ]
data class TrainerImageDto(
    val image: ImageDto?
)
