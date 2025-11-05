package com.example.teene.events.data.repositories

import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.events.data.models.EventDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EventsRepositoryImpl(
    private val apiService: AuthorizedApiService
) {
    fun getEvents(
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Int? = null,
        intensities: List<String>? = null,
        maxPrice: Int? = null,
        multiDay: Boolean? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): Flow<Result<List<EventDto>>> = flow {
        try {
            val response = apiService.getEvents(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                intensities = intensities,
                maxPrice = maxPrice,
                multiDay = multiDay,
                page = page,
                pageSize = pageSize
            )
            if (response.isSuccessful) {
                emit(Result.success(response.body() ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Failed to fetch events: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}