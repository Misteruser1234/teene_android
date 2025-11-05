package com.example.teene.events.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.events.domain.models.Event
import com.example.teene.events.domain.usecases.GetEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EventsUiState {
    data object Initial : EventsUiState()
    data object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val throwable: Throwable?) : EventsUiState()
}

class EventsViewModel(
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Initial)
    val uiState: StateFlow<EventsUiState> = _uiState

    init {
        // Load with no filters by default
        fetchEvents()
    }

    fun fetchEvents(
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Int? = null,
        intensities: List<String>? = null,
        maxPrice: Int? = null,
        multiDay: Boolean? = null,
        page: Int? = null,
        pageSize: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.emit(EventsUiState.Loading)
            getEventsUseCase.execute(
                GetEventsUseCase.Params(
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    intensities = intensities,
                    maxPrice = maxPrice,
                    multiDay = multiDay,
                    page = page,
                    pageSize = pageSize
                )
            ).collect { result ->
                if (result.isSuccess) {
                    _uiState.emit(EventsUiState.Success(result.getOrNull().orEmpty()))
                } else {
                    _uiState.emit(EventsUiState.Error(result.exceptionOrNull()))
                }
            }
        }
    }
}