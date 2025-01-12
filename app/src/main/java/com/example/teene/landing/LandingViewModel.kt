package com.example.teene.landing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.LandingDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LandingViewModel(private val landingDataStore: LandingDataStore) : ViewModel() {

    val isLandingSeen = landingDataStore.isLandingSeen.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun saveLandingSeen() {
        Log.i("BOBAN","@@")
        viewModelScope.launch {
            landingDataStore.updateLandingSeen()
        }
    }
}