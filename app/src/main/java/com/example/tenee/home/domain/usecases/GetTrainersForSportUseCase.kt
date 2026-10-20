package com.example.tenee.home.domain.usecases

import com.example.tenee.home.data.models.TrainerResponseItem
import com.example.tenee.home.data.repositories.TrainersRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by 3100lari on 2025/06/12
 */
class GetTrainersForSportUseCase(
    private val trainersRepository: TrainersRepository,
)
{
    fun execute(sportId: Int): Flow<Result<List<TrainerResponseItem>>>
    {
        return trainersRepository.getTrainersForSport(sportId)  // Returns Flow from the repository
    }
}