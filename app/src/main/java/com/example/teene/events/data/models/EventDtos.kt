package com.example.teene.events.data.models

import com.google.gson.annotations.SerializedName

data class ImageDto(
    val url: String?
)

data class EventTypeDto(
    val id: Int,
    val name: String,
    val price: Double,
    @SerializedName("max_participants") val maxParticipants: Int,
    val participants: Int
)

data class EventDto(
    val id: Int,
    val name: String,
    val about: String?,
    @SerializedName("date_from") val dateFrom: String?,
    @SerializedName("date_to") val dateTo: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val city: String?,
    val currency: String?,
    val intensities: List<String>?,
    @SerializedName("with_coach") val withCoach: Boolean?,
    val image: ImageDto?,
    @SerializedName("event_types") val eventTypes: List<EventTypeDto>?
)