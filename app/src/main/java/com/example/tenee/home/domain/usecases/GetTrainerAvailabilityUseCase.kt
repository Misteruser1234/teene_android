package com.example.tenee.home.domain.usecases

import com.example.tenee.home.data.models.TrainerAvailabilityResponse
import com.example.tenee.home.data.repositories.BookRepositoryImpl
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GetTrainerAvailabilityUseCase(
    private val repository: BookRepositoryImpl
) {


    operator fun invoke(
        trainerId: Int,
        startDate: LocalDate? = LocalDate.now(),
        endDate: LocalDate? = LocalDate.now()
    ): Flow<Result<TrainerAvailabilityResponse>> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val startDateStr = startDate?.format(formatter)
        val endDateStr = endDate?.format(formatter)
        return repository.fetchTrainerAvailability(trainerId, startDateStr, endDateStr)
    }
}