package com.example.teene.home.presentation.models

import com.example.teene.home.data.models.FeatureItem

/**
 * Created by 3100lari on 2025/06/12
 */
data class TrainerWithCoordinates(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val latitude: Double,
    val longitude: Double,
    val rate: Double,
    val currency: String,
    val address: String,
    val distance: Double,
    val rating: Double?,
    val about: String,
    val imageUrl: String?,
    val features: List<FeatureItem>
)