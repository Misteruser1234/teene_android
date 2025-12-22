package com.example.teene.home.data.datasources

import com.example.teene.data.UserDataStore
import kotlinx.coroutines.flow.firstOrNull

class BookingsLocalDataSource(
    private val userDataStore: UserDataStore
) {
    suspend fun getCurrentUserId(): Int? = userDataStore.userIdFlow.firstOrNull()
}