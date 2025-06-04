package com.example.teene.home.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by 3100lari on 2025/05/29
 */
data class SportsResponseItem(
    val id: Int,
    @SerializedName("image_url") val imageUrl: String,
    val name: String,
    @SerializedName("trainer_count") val trainerCount: Int
)