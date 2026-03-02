package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.TrainingBookingDto
import com.example.teene.home.data.repositories.TrainingBookingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrainingBookingsUseCase(
    private val repository: TrainingBookingsRepository
) {
    operator fun invoke(): Flow<Result<List<TrainingBookingDto>>> = flow {
        emit(repository.getTrainingBookings())
    }
}
