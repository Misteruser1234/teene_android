package com.example.teene.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by 3100lari on 2025/02/16
 */
class ExploreViewModel(
    getSportsUseCase: GetSportsUseCase
) : ViewModel()
{


    val sportsList: StateFlow<List<SportWithTrainers>> = getSportsUseCase.execute()
        .map { result ->
            result.fold(
                onSuccess = { sports ->
                    sports.map { sport ->
                        SportWithTrainers(
                            id = sport.id,
                            sport.name,
                            TrainerNumberCategory.Companion.fromNumber(sport.trainerCount),
                            imageUrl = sport.image.url // Ensure correct type
                        )
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun onSportItemClick(id: Int)
    {
        Log.i("BOBAN","Clicked Sport item with ID: $id")
    }
}