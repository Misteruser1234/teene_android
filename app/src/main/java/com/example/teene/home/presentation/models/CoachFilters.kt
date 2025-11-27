package com.example.teene.home.presentation.models

/**
 * Filters for coaches search/browse.
 * Extend as needed when backend/server-side filtering becomes available.
 * Created by 3100lari on 2025/11/05
 */
data class CoachFilters(
    val sportId: Int? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val minRating: Double? = null,
    val maxDistanceKm: Double? = null,
    val featureIds: Set<Int> = emptySet(),
)
