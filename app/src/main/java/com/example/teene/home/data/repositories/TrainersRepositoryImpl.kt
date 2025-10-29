package com.example.teene.home.data.repositories

import android.util.Log
import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.TrainerResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by 3100lari on 2025/06/12
 */
class TrainersRepositoryImpl(
    private val apiService: AuthorizedApiService
)
{
    fun getTrainersForSport(sportId: Int): Flow<Result<List<TrainerResponseItem>>> = flow {
        try
        {
            val response = apiService.getTrainersBySportId(sportId)
            if (response.isSuccessful)
            {
                Log.i("SportsRepository", "Sports fetched successfully: ${response.body()}")
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