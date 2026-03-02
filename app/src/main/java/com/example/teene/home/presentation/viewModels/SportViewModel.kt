package com.example.teene.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.domain.usecases.GetTrainersForSportUseCase
import com.example.teene.home.presentation.models.TrainerWithCoordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by 3100lari on 2025/06/12
 */
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.presentation.models.SportUi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class SportViewModel(
    private val getTrainersForSportUseCase: GetTrainersForSportUseCase,
    private val getSportsUseCase: GetSportsUseCase
) : ViewModel()  // Assuming ViewModel is imported from androidx.lifecycle)
{
    private val _selectedSportId = MutableStateFlow(0)
    val selectedSportId: StateFlow<Int> = _selectedSportId.asStateFlow()

    val sports: StateFlow<List<SportUi>> = getSportsUseCase.execute()
        .map { result ->
            result.fold(
                onSuccess = { list ->
                    list.map { SportUi(id = it.id, name = it.name) }
                },
                onFailure = { emptyList() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Selected sport name derived from current list and selected id
    val selectedSportName: StateFlow<String?> = combine(sports, selectedSportId) { list, id ->
        list.firstOrNull { it.id == id }?.name
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    val trainersList: StateFlow<List<TrainerWithCoordinates>> = selectedSportId
        .flatMapLatest { sportId ->
            getTrainersForSportUseCase.execute(sportId)
        }
        .map { result ->
            result.fold(
                onSuccess = { trainer ->
                    trainer.map { trainer ->
                        TrainerWithCoordinates(
                            id = trainer.id,
                            firstName = trainer.firstName,
                            lastName = trainer.lastName,
                            latitude = trainer.latitude,
                            longitude = trainer.longitude,
                            rate = trainer.rate,
                            currency = trainer.currency,
                            address = trainer.address,
                            distance = trainer.distance,
                            imageUrl = trainer.images.firstOrNull()?.image?.url, // Updated for multiple images
                            rating = trainer.rating ?: 0.0, // Default to 0.0 if null
                            about = trainer.about ?: "", // Default to empty string if null
                            features = trainer.features ?: emptyList() // Default to empty list if null

                        )
                    }
                },
                onFailure = { emptyList() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun loadTrainersForSport(sportId: Int)
    {
        _selectedSportId.value = sportId
    }

}