package com.example.tenee.home.presentation.models

import com.example.tenee.home.data.models.TrainerAvailabilityResponse

sealed class TrainerAvailabilityUiState {
    object Loading : TrainerAvailabilityUiState()
    sealed class Success : TrainerAvailabilityUiState() {
        data class Available(val availableSlots: List<String>) : Success()
        object Empty : Success()
    }
    data class Error(val message: String) : TrainerAvailabilityUiState()
}