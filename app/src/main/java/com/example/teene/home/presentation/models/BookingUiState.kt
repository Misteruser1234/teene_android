package com.example.teene.home.presentation.models

import com.example.teene.home.data.models.TrainingBookingResponse

sealed class BookingUiState {
    data object Idle : BookingUiState()
    data object Loading : BookingUiState()
    data class Success(val response: TrainingBookingResponse) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}
