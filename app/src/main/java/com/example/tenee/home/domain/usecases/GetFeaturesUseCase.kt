package com.example.tenee.home.domain.usecases

import com.example.tenee.home.data.models.FeatureItem
import com.example.tenee.home.data.repositories.FeaturesRepositoryImpl
import kotlinx.coroutines.flow.Flow

class GetFeaturesUseCase(
    private val featuresRepository: FeaturesRepositoryImpl
) {
    fun execute(): Flow<Result<List<FeatureItem>>> = featuresRepository.getFeatures()
}
