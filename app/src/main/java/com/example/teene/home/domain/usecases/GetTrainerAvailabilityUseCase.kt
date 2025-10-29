package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.TrainerAvailabilityResponse
import com.example.teene.home.data.repositories.BookRepositoryImpl
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