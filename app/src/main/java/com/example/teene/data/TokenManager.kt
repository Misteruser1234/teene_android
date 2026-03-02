package com.example.teene.data

/**
 * Created by 3100lari on 2025/02/09
 */
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val dataStore: DataStore<Preferences>) {

    // Define the key for the token
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // Save the token to Datastore
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Retrieve the token from Datastore
    val getToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY] // Return the token if it exists
        }

    // Clear the token (for logout, etc.)
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}
