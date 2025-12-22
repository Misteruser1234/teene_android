package com.example.teene.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.domain.usecases.BookTrainingUseCase
import com.example.teene.home.domain.usecases.GetTrainerAvailabilityUseCase
import com.example.teene.home.presentation.models.TrainerAvailabilityUiState
import com.example.teene.home.presentation.models.BookingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookingTrainerViewModel(
    private val getTrainerAvailabilityUseCase: GetTrainerAvailabilityUseCase,
    private val bookTrainingUseCase: BookTrainingUseCase
) : ViewModel()
{

    private val _availabilityState =
        MutableStateFlow<TrainerAvailabilityUiState>(TrainerAvailabilityUiState.Success.Empty)
    val availabilityState: StateFlow<TrainerAvailabilityUiState> = _availabilityState

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState

    fun fetchTrainerAvailability(
        trainerId: Int,
        startDate: LocalDate? = LocalDate.now(),
        endDate: LocalDate? = LocalDate.now()
    )
    {
        viewModelScope.launch {
            getTrainerAvailabilityUseCase(trainerId, startDate, endDate)
                .map { result ->
                    result.fold(
                        onSuccess = { response ->
                            response.entries
                                .firstOrNull()?.let {
                                    TrainerAvailabilityUiState.Success.Available(it.value)
                                } ?: TrainerAvailabilityUiState.Success.Empty

                        },
                        onFailure = {
                            TrainerAvailabilityUiState.Error(
                                it.message ?: "Unknown error"
                            )
                        }
                    )
                }
                .onStart { emit(TrainerAvailabilityUiState.Loading) }
                .catch { emit(TrainerAvailabilityUiState.Error(it.message ?: "Unknown error")) }
                .collectLatest { _availabilityState.value = it }
        }
    }

    fun bookSession(trainerId: Int, selectedDateMillis: Long, selectedTimeHHmm: String) {
        viewModelScope.launch {
            _bookingState.value = BookingUiState.Loading
            val result = bookTrainingUseCase(trainerId, selectedDateMillis, selectedTimeHHmm)
            _bookingState.value = result.fold(
                onSuccess = { BookingUiState.Success(it) },
                onFailure = { BookingUiState.Error(it.message ?: "Booking failed") }
            )
        }
    }
}