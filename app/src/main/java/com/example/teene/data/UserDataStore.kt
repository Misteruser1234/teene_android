package com.example.teene.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores user-related local data for the authorized user (e.g., user_id).
 */
class UserDataStore(private val appContext: Context) {

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private object Keys {
        val USER_ID: Preferences.Key<Int> = intPreferencesKey("user_id")
    }

    val userIdFlow: Flow<Int?> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    suspend fun saveUserId(userId: Int) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
