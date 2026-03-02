package com.example.teene.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores user-related local data for the authorized user (e.g., user_id).
 * Also stores per-user onboarding flags.
 * Receives a singleton DataStore<Preferences> via DI.
 */
class UserDataStore(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val USER_ID: Preferences.Key<Int> = intPreferencesKey("user_id")
        val TRAINER_ONBOARDING_SELECTED: Preferences.Key<Boolean> = booleanPreferencesKey("trainer_onboarding_selected")
    }

    val userIdFlow: Flow<Int?> = dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    val trainerOnboardingSelected: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.TRAINER_ONBOARDING_SELECTED] ?: false
    }

    suspend fun setTrainerOnboardingSelected(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.TRAINER_ONBOARDING_SELECTED] = value
        }
    }

    suspend fun saveUserId(userId: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
