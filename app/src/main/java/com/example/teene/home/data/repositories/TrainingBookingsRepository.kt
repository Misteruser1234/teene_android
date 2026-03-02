package com.example.teene.home.data.repositories

import com.example.teene.home.data.models.TrainingBookingResponse

interface TrainingBookingsRepository {
    /**
     * Creates a training booking for the currently logged-in user.
     * @param trainerId trainer identifier
     * @param startTimeUtc formatted start time in UTC: "yyyy-MM-dd HH:mm:ss UTC"
     */
    suspend fun bookTraining(trainerId: Int, startTimeUtc: String): Result<TrainingBookingResponse>

    /**
     * Lists training bookings for the current authorized user.
     */
    suspend fun getTrainingBookings(): Result<List<com.example.teene.home.data.models.TrainingBookingDto>>
}