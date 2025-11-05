package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.TrainerResponseItem
import com.example.teene.home.data.repositories.TrainersRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to fetch all trainers across all sports.
 * Created by 3100lari on 2025/11/05
 */
class GetAllTrainersUseCase(
    private val trainersRepository: TrainersRepository,
) {
    fun execute(): Flow<Result<List<TrainerResponseItem>>> = trainersRepository.getAllTrainers()
}
