package com.example.teene.home.data.repositories

import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.FeatureItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for loading Features from authorized API.
 */
class FeaturesRepositoryImpl(
    private val apiService: AuthorizedApiService
) {
    fun getFeatures(): Flow<Result<List<FeatureItem>>> = flow {
        try {
            val response = apiService.getFeatures()
            if (response.isSuccessful) {
                emit(Result.success(response.body() ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Failed to fetch features: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
