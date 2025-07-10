package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.SportsResponseItem
import com.example.teene.home.data.models.TrainerResponseItem
import com.example.teene.home.data.repositories.SportsRepositoryImpl
import com.example.teene.home.data.repositories.TrainersRepositoryImpl
import kotlinx.coroutines.flow.Flow

/**
 * Created by 3100lari on 2025/06/12
 */
class GetTrainersForSportUseCase(
    private val trainerRepositoryImpl: TrainersRepositoryImpl,
    )
{
    fun execute(sportId: Int): Flow<Result<List<TrainerResponseItem>>>
    {
        return trainerRepositoryImpl.getTrainersForSport(sportId)  // Returns Flow from the repository
    }
}