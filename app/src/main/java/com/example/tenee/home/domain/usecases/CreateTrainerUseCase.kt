package com.example.tenee.home.domain.usecases

import com.example.tenee.home.data.models.TrainerCreateRequest
import com.example.tenee.home.data.models.TrainerCreateResponse
import com.example.tenee.home.data.repositories.TrainersRepository
import kotlinx.coroutines.flow.Flow

class CreateTrainerUseCase(
    private val repository: TrainersRepository
) {
    fun execute(request: TrainerCreateRequest): Flow<Result<TrainerCreateResponse>> {
        return repository.createTrainer(request)
    }
}
