package com.example.teene.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.R
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory
import com.example.teene.ui.viewModel.LoginUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by 3100lari on 2025/02/16
 */
class ExploreViewModel(
//    private val getSportsUseCase: GetSportsUseCase
) : ViewModel()
{
    private val _sportsList = MutableStateFlow<List<SportWithTrainers>>(emptyList())

//    val sportsList: StateFlow<List<SportWithTrainers>> = getSportsUseCase.execute()
//        .map { result ->
//            result.fold(
//                onSuccess = { sportsResponse ->
//                    sportsResponse.sports.map { sport ->
//                        SportWithTrainers(
//                            sport.name,
//                            TrainerNumberCategory.fromNumber(sport.trainerCount),
//                            imageUrl = sport.imageUrl // Ensure correct type
//                        )
//                    }
//                },
//                onFailure = {
//                    emptyList()
//                }
//            )
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = kotlinx.coroutines.flow.SharingStarted.Lazily,
//            initialValue = emptyList()
//        )

    init
    {
        _sportsList.value = listOf(
            SportWithTrainers(
                "Paddel",
                TrainerNumberCategory.TEN_PLUS,
//                R.drawable.sport_paddel
            ),
            SportWithTrainers(
                "Climbing",
                TrainerNumberCategory.FIFTY_PLUS,
//                R.drawable.sport_climbing
            ),
            SportWithTrainers(
                "Powerlifting",
                TrainerNumberCategory.HUNDRED_PLUS,
//                R.drawable.sport_powerlifting
            ),
            SportWithTrainers(
                "Taekwondo",
                TrainerNumberCategory.THOUSAND_PLUS,
//                R.drawable.sport_taekwondo
            )
        )
    }
}

