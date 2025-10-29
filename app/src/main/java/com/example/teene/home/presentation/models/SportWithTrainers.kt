package com.example.teene.home.presentation.models


/**
 * Created by 3100lari on 2025/02/15
 */
data class SportWithTrainers(
    val id: Int,
    val sportName: String,
    val trainerNumberCategory: TrainerNumberCategory,
    val imageUrl: String = "",
)
