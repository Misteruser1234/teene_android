package com.example.teene.home.data.repositories

import android.util.Log
import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.TrainerCreateRequest
import com.example.teene.home.data.models.TrainerCreateResponse
import com.example.teene.home.data.models.TrainerResponseItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by 3100lari on 2025/06/12
 */
class TrainersRepositoryImpl(
    private val apiService: AuthorizedApiService
) : TrainersRepository {
    override fun getTrainersForSport(sportId: Int): Flow<Result<List<TrainerResponseItem>>> = flow {
        try {
            val response = apiService.getTrainersBySportId(sportId)
            if (response.isSuccessful) {
                Log.i("TrainersRepository", "Trainers fetched successfully: ${response.body()}")
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch trainers: ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e("TrainersRepositoryImpl", "Failed to fetch trainers", e)
        }
    }

    override fun getAllTrainers(): Flow<Result<List<TrainerResponseItem>>> = flow {
        try {
            val sportsResponse = apiService.getSports()
            if (!sportsResponse.isSuccessful) {
                emit(Result.failure(Exception("Failed to fetch sports: ${sportsResponse.message()}")))
                return@flow
            }
            val sports = sportsResponse.body().orEmpty()
            val combined: List<TrainerResponseItem> = coroutineScope {
                sports.map { sport ->
                    async {
                        try {
                            val trainersResp = apiService.getTrainersBySportId(sport.id)
                            if (trainersResp.isSuccessful) trainersResp.body().orEmpty() else emptyList()
                        } catch (e: Exception) {
                            Log.e("TrainersRepositoryImpl", "Per-sport trainers fetch failed for id=${sport.id}", e)
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()
            }
            emit(Result.success(combined))
        } catch (e: Exception) {
            Log.e("TrainersRepositoryImpl", "Failed to fetch all trainers", e)
            emit(Result.failure(e))
        }
    }

    override fun createTrainer(request: TrainerCreateRequest): Flow<Result<TrainerCreateResponse>> = flow {
        try {
            val response = apiService.createTrainer(request)
            if (response.isSuccessful) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to create trainer: ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e("TrainersRepositoryImpl", "Failed to create trainer", e)
            emit(Result.failure(e))
        }
    }
}