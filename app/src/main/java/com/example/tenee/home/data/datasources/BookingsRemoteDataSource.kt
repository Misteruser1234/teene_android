package com.example.tenee.home.data.datasources

import com.example.tenee.data.network.AuthorizedApiService
import com.example.tenee.home.data.models.TrainingBookingRequest
import com.example.tenee.home.data.models.TrainingBookingResponse
import retrofit2.Response

class BookingsRemoteDataSource(
    private val api: AuthorizedApiService
) {
    suspend fun createTrainingBooking(request: TrainingBookingRequest): Response<TrainingBookingResponse> =
        api.createTrainingBooking(request)

    suspend fun getTrainingBookings(): Response<List<com.example.tenee.home.data.models.TrainingBookingDto>> =
        api.getTrainingBookings()
}