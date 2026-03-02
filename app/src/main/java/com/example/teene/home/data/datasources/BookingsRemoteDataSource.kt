package com.example.teene.home.data.datasources

import com.example.teene.data.network.AuthorizedApiService
import com.example.teene.home.data.models.TrainingBookingRequest
import com.example.teene.home.data.models.TrainingBookingResponse
import retrofit2.Response

class BookingsRemoteDataSource(
    private val api: AuthorizedApiService
) {
    suspend fun createTrainingBooking(request: TrainingBookingRequest): Response<TrainingBookingResponse> =
        api.createTrainingBooking(request)

    suspend fun getTrainingBookings(): Response<List<com.example.teene.home.data.models.TrainingBookingDto>> =
        api.getTrainingBookings()
}