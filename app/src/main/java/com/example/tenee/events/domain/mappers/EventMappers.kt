package com.example.tenee.events.domain.mappers

import com.example.tenee.events.data.models.EventDto
import com.example.tenee.events.data.models.EventTypeDto
import com.example.tenee.events.domain.models.Event
import com.example.tenee.events.domain.models.EventType

fun EventDto.toDomain(): Event = Event(
    id = id,
    name = name,
    about = about,
    dateFrom = dateFrom,
    dateTo = dateTo,
    latitude = latitude,
    longitude = longitude,
    address = address,
    city = city,
    currency = currency,
    intensities = intensities ?: emptyList(),
    withCoach = withCoach,
    imageUrl = image?.url,
    eventTypes = eventTypes?.map { it.toDomain() } ?: emptyList()
)

fun EventTypeDto.toDomain(): EventType = EventType(
    id = id,
    name = name,
    price = price,
    maxParticipants = maxParticipants,
    participants = participants
)