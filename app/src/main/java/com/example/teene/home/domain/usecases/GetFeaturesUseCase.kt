package com.example.teene.home.domain.usecases

import com.example.teene.home.data.models.FeatureItem
import com.example.teene.home.data.repositories.FeaturesRepositoryImpl
import kotlinx.coroutines.flow.Flow

class GetFeaturesUseCase(
    private val featuresRepository: FeaturesRepositoryImpl
) {
    fun execute(): Flow<Result<List<FeatureItem>>> = featuresRepository.getFeatures()
}
