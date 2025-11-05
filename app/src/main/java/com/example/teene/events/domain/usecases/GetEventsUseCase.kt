package com.example.teene.events.domain.usecases

import com.example.teene.events.data.repositories.EventsRepositoryImpl
import com.example.teene.events.domain.mappers.toDomain
import com.example.teene.events.domain.models.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetEventsUseCase(
    private val repository: EventsRepositoryImpl
) {
    data class Params(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val radius: Int? = null,
        val intensities: List<String>? = null,
        val maxPrice: Int? = null,
        val multiDay: Boolean? = null,
        val page: Int? = null,
        val pageSize: Int? = null
    )

    fun execute(params: Params = Params()): Flow<Result<List<Event>>> =
        repository.getEvents(
            latitude = params.latitude,
            longitude = params.longitude,
            radius = params.radius,
            intensities = params.intensities,
            maxPrice = params.maxPrice,
            multiDay = params.multiDay,
            page = params.page,
            pageSize = params.pageSize
        ).map { result ->
            result.map { list -> list.map { it.toDomain() } }
        }
}