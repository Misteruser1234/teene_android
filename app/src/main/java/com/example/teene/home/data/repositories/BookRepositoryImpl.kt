package com.example.teene.home.data.repositories

import android.util.Log
import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.TrainerAvailabilityResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by 3100lari on 2025/08/19
 */



class BookRepositoryImpl(
    private val apiService: AuthorizedApiService
)
{
    fun fetchTrainerAvailability(
        trainerId: Int,
        startDate: String? = null,
        endDate: String? = null
    ): Flow<Result<TrainerAvailabilityResponse>> = flow {
        try
        {
            val response = apiService.getTrainerAvailability(
                trainerId = trainerId,
                startDate = startDate,
                endDate = endDate
            )
            if (response.isSuccessful)
            {
                Log.i("BookRepository", "Trainer availability fetched: ${response.body()}")
                emit(Result.success(response.body()!!))
            }
            else
            {
                emit(Result.failure(Exception("Failed to fetch trainer availability: ${response.message()}")))
            }
        } catch (e: Exception)
        {
            emit(Result.failure(e))
        }
    }
}
