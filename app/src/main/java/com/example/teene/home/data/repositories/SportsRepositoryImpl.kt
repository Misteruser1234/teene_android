package com.example.teene.home.data.repositories

import com.example.teene.data.network.AuthorizedApiService
import kotlinx.coroutines.flow.Flow
import com.example.teene.home.data.models.SportsResponse
import kotlinx.coroutines.flow.flow

class SportsRepositoryImpl(
    private val apiService: AuthorizedApiService
)
{
    fun getSports(): Flow<Result<SportsResponse>> = flow {
        try
        {
            val response = apiService.getSports()
            if (response.isSuccessful)
            {
                emit(Result.success(response.body()!!))
            }
            else
            {
                emit(Result.failure(Exception("Failed to fetch sports: ${response.message()}")))
                // Emit failure result
            }
        } catch (e: Exception)
        {
            emit(Result.failure(e))  // Emit failure in case of an exception
        }

    }
}