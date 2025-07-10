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
class SportViewModel(
    private val getTrainersForSportUseCase: GetTrainersForSportUseCase
) : ViewModel()  // Assuming ViewModel is imported from androidx.lifecycle)
{
    private val selectedSportId = MutableStateFlow<Int>(0)

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
                            imageUrl = trainer.image.url, // Ensure correct type
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
        selectedSportId.value = sportId
    }

}