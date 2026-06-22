package com.example.teene.home.data.repositories

import com.example.teene.home.data.models.TrainerCreateRequest
import com.example.teene.home.data.models.TrainerCreateResponse
import com.example.teene.home.data.models.TrainerResponseItem
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for providing trainers data.
 * Created by 3100lari on 2025/11/05
 */
interface TrainersRepository {
    fun getTrainersForSport(sportId: Int): Flow<Result<List<TrainerResponseItem>>>

    /**
     * Fetch all trainers across all sports.
     * Default implementation may aggregate results from per-sport calls when a direct API is not present.
     */
    fun getAllTrainers(): Flow<Result<List<TrainerResponseItem>>>

    fun createTrainer(request: TrainerCreateRequest): Flow<Result<TrainerCreateResponse>>
}
