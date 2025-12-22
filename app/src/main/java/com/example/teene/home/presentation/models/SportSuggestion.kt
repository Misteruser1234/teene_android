package com.example.teene.home.presentation.models

/**
 * UI model for sport suggestions shown while typing in the search bar.
 */
data class SportSuggestion(
    val id: Int,
    val name: String,
    val trainerCount: Int?
)
