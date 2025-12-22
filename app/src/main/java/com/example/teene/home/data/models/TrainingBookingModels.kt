package com.example.teene.home.data.models

/**
 * Request/Response models for creating training bookings.
 */

data class TrainingBookingRequest(
    val user_id: Int,
    val trainer_id: Int,
    val start_time: String
)

/**
 * Minimal response model; extend if backend returns more fields.
 */
data class TrainingBookingResponse(
    val id: Int? = null,
    val status: String? = null,
    val message: String? = null
)
