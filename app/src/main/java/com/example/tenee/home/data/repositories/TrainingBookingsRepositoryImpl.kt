package com.example.tenee.home.data.repositories

import com.example.tenee.home.data.datasources.BookingsLocalDataSource
import com.example.tenee.home.data.datasources.BookingsRemoteDataSource
import com.example.tenee.home.data.models.TrainingBookingRequest
import com.example.tenee.home.data.models.TrainingBookingResponse

class TrainingBookingsRepositoryImpl(
    private val local: BookingsLocalDataSource,
    private val remote: BookingsRemoteDataSource,
) : TrainingBookingsRepository {

    override suspend fun bookTraining(trainerId: Int, startTimeUtc: String): Result<TrainingBookingResponse> {
        return try {
            val userId = local.getCurrentUserId()
                ?: return Result.failure(IllegalStateException("No user_id saved locally"))

            val response = remote.createTrainingBooking(
                TrainingBookingRequest(
                    user_id = userId,
                    trainer_id = trainerId,
                    start_time = startTimeUtc
                )
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: TrainingBookingResponse(status = "created"))
            } else {
                Result.failure(Exception("Booking failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrainingBookings(): Result<List<com.example.tenee.home.data.models.TrainingBookingDto>> {
        return try {
            val response = remote.getTrainingBookings()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load training bookings: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
