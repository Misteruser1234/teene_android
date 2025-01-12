package com.example.teene.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LandingDataStore(val appContext: Context) {

    private val Context.dataStore by preferencesDataStore(name = "application_prefferences")

    private val IS_LANDING_SEEN = booleanPreferencesKey("is_landing_seen")

    // Function to save the boolean value
    suspend fun updateLandingSeen() {
        appContext.dataStore.edit { preferences ->
            preferences[IS_LANDING_SEEN] = true
        }
    }

    // Function to get the saved boolean value
    val isLandingSeen: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            preferences[IS_LANDING_SEEN] ?: false
        }
}