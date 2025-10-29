package com.example.teene.home.domain.usecases

/**
 * Created by 3100lari on 2025/05/29
 */

import com.example.teene.home.data.models.SportsResponseItem
import com.example.teene.home.data.repositories.SportsRepositoryImpl
import kotlinx.coroutines.flow.Flow


class GetSportsUseCase(private val sportsRepository: SportsRepositoryImpl)
{
    fun execute(): Flow<Result<List<SportsResponseItem>>>
    {
        return sportsRepository.getSports()  // Returns Flow from the repository
    }
}
