package com.example.tenee.home.domain.usecases

import com.example.tenee.home.data.models.TrainingBookingDto
import com.example.tenee.home.data.repositories.TrainingBookingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrainingBookingsUseCase(
    private val repository: TrainingBookingsRepository
) {
    operator fun invoke(): Flow<Result<List<TrainingBookingDto>>> = flow {
        emit(repository.getTrainingBookings())
    }
}
