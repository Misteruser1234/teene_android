package com.example.tenee.home.data.datasources

import com.example.tenee.data.UserDataStore
import kotlinx.coroutines.flow.firstOrNull

class BookingsLocalDataSource(
    private val userDataStore: UserDataStore
) {
    suspend fun getCurrentUserId(): Int? = userDataStore.userIdFlow.firstOrNull()
}