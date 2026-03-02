package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.LandingDataStore
import com.example.teene.data.TokenManager
import com.example.teene.data.UserDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LandingViewModel(
    tokenManager: TokenManager,
    private val landingDataStore: LandingDataStore,
    private val userDataStore: UserDataStore
) : ViewModel() {

    val tokenExists = tokenManager.getToken.map {
        it != null && it.isNotEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    val needsPostSignupChoice = landingDataStore.needsPostSignupChoice.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val trainerOnboardingSelected = userDataStore.trainerOnboardingSelected.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    fun setNeedsPostSignupChoice(value: Boolean) {
        viewModelScope.launch {
            landingDataStore.setNeedsPostSignupChoice(value)
        }
    }

    fun setTrainerOnboardingSelected(value: Boolean) {
        viewModelScope.launch {
            userDataStore.setTrainerOnboardingSelected(value)
        }
    }

    fun saveLandingSeen() {
        viewModelScope.launch {
//            landingDataStore.updateLandingSeen()
        }
    }
}