package com.example.teene.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LandingDataStore(private val dataStore: DataStore<Preferences>) {

    private val IS_LANDING_SEEN = booleanPreferencesKey("is_landing_seen")
    private val NEEDS_POST_SIGNUP_CHOICE = booleanPreferencesKey("needs_post_signup_choice")

    // Function to save the boolean value
    suspend fun updateLandingSeen() {
        dataStore.edit { preferences ->
            preferences[IS_LANDING_SEEN] = true
        }
    }

    // Function to get the saved boolean value
    val isLandingSeen: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LANDING_SEEN] ?: false
        }

    // Post-signup choice flag: when true, app should show choice screen even if token exists
    suspend fun setNeedsPostSignupChoice(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[NEEDS_POST_SIGNUP_CHOICE] = value
        }
    }

    val needsPostSignupChoice: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[NEEDS_POST_SIGNUP_CHOICE] ?: false
        }
}