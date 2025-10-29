package com.example.teene.home.presentation.models

import com.example.teene.home.data.models.TrainerAvailabilityResponse

sealed class TrainerAvailabilityUiState {
    object Loading : TrainerAvailabilityUiState()
    sealed class Success : TrainerAvailabilityUiState() {
        data class Available(val availableSlots: List<String>) : Success()
        object Empty : Success()
    }
    data class Error(val message: String) : TrainerAvailabilityUiState()
}