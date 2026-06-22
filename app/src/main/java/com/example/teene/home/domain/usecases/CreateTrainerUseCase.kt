package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.TrainerCreateRequest
import com.example.teene.home.data.models.TrainerCreateResponse
import com.example.teene.home.data.repositories.TrainersRepository
import kotlinx.coroutines.flow.Flow

class CreateTrainerUseCase(
    private val repository: TrainersRepository
) {
    fun execute(request: TrainerCreateRequest): Flow<Result<TrainerCreateResponse>> {
        return repository.createTrainer(request)
    }
}
