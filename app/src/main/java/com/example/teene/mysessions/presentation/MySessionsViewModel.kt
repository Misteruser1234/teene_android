package com.example.teene.mysessions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.data.models.TrainingBookingDto
import com.example.teene.home.domain.usecases.GetTrainingBookingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed interface MySessionsUiState {
    data object Idle : MySessionsUiState
    data object Loading : MySessionsUiState
    data class Success(val bookings: List<TrainingBookingDto>) : MySessionsUiState
    data class Error(val message: String) : MySessionsUiState
}

class MySessionsViewModel(
    private val getTrainingBookings: GetTrainingBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MySessionsUiState>(MySessionsUiState.Idle)
    val uiState: StateFlow<MySessionsUiState> = _uiState

    fun fetchBookings() {
        viewModelScope.launch {
            getTrainingBookings()
                .map { result ->
                    result.fold(
                        onSuccess = { MySessionsUiState.Success(it) },
                        onFailure = { MySessionsUiState.Error(it.message ?: "Failed to load sessions") }
                    )
                }
                .onStart { emit(MySessionsUiState.Loading) }
                .catch { emit(MySessionsUiState.Error(it.message ?: "Failed to load sessions")) }
                .collectLatest { _uiState.value = it }
        }
    }
}
