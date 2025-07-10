package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.LandingDataStore
import com.example.teene.data.TokenManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LandingViewModel(tokenManager: TokenManager) : ViewModel() {

    val tokenExists = tokenManager.getToken.map {
        it != null && it.isNotEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    fun saveLandingSeen() {
        viewModelScope.launch {
//            landingDataStore.updateLandingSeen()
        }
    }
}