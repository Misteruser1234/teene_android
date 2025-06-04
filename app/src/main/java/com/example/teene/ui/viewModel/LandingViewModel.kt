package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.LandingDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LandingViewModel(private val landingDataStore: LandingDataStore) : ViewModel() {

    val isLandingSeen = landingDataStore.isLandingSeen.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    fun saveLandingSeen() {
        viewModelScope.launch {
            landingDataStore.updateLandingSeen()
        }
    }
}