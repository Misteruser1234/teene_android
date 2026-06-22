package com.example.teene.home.data.models

import com.google.gson.annotations.SerializedName

data class TrainerCreateRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("sport_id") val sportId: Int? = null,
    val about: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val rate: Int? = null,
    val currency: String? = null,
    val intensities: List<String>? = null,
    @SerializedName("highest_education") val highestEducation: List<String>? = null,
    val languages: List<String>? = null,
    val achievements: List<String>? = null,
    @SerializedName("career_history") val careerHistory: List<CareerHistoryRequest>? = null,
    @SerializedName("feature_ids") val featureIds: List<Int>? = null
)

data class CareerHistoryRequest(
    @SerializedName("company_name") val companyName: String,
    val from: String,
    val to: String
)

data class TrainerCreateResponse(
    val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val rate: Double?,
    val currency: String?,
    val address: String?,
    val distance: Double?,
    val rating: Double?,
    @SerializedName("review_count") val reviewCount: Int,
    val about: String,
    @SerializedName("career_history") val careerHistory: List<CareerHistoryResponse>,
    val achievements: List<String>,
    val languages: List<String>,
    @SerializedName("highest_education") val highestEducation: List<String>,
    @SerializedName("quickblox_id") val quickbloxId: String?,
    val images: List<TrainerImageDto>,
    val features: List<FeatureItem>,
    val sport: SportDto?
)

data class CareerHistoryResponse(
    @SerializedName("company_name") val companyName: String,
    val from: String,
    val to: String
)
