package com.example.teene.home.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by 3100lari on 2025/06/12
 */
data class TrainerResponseItem(
    val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val latitude: Double,
    val longitude: Double,
    val rate: Double,
    val currency: String,
    val address: String,
    val distance: Double,
    val images: List<TrainerImageItem>,
    val rating: Double?,
    val about: String,
    val features: List<FeatureItem>,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)

// Wrapper to match API structure: trainer -> images -> image -> url
data class TrainerImageItem(
    val image: ImageItem?
)