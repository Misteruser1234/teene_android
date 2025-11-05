package com.example.teene.events.domain.models

data class Event(
    val id: Int,
    val name: String,
    val about: String?,
    val dateFrom: String?,
    val dateTo: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val city: String?,
    val currency: String?,
    val intensities: List<String>,
    val withCoach: Boolean?,
    val imageUrl: String?,
    val eventTypes: List<EventType>
)

data class EventType(
    val id: Int,
    val name: String,
    val price: Double,
    val maxParticipants: Int,
    val participants: Int
)