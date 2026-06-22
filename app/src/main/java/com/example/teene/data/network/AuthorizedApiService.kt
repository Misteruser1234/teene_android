package com.example.teene.data.network

import com.example.teene.events.data.models.EventDto
import com.example.teene.home.data.models.FeatureItem
import com.example.teene.home.data.models.SportsResponseItem
import com.example.teene.home.data.models.TrainerAvailabilityResponse
import com.example.teene.home.data.models.TrainerCreateRequest
import com.example.teene.home.data.models.TrainerCreateResponse
import com.example.teene.home.data.models.TrainerResponseItem
import com.example.teene.home.data.models.TrainingBookingRequest
import com.example.teene.home.data.models.TrainingBookingResponse
import com.example.teene.home.data.models.UploadedImageDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by 3100lari on 2025/05/29
 */
interface AuthorizedApiService
{

    @GET("/sports")
    suspend fun getSports(
        @Query("radius") radius: Int? = null,
        @Query("feature_ids") featureIds: List<Int>? = null,
        @Query("intensities") intensities: List<String>? = null,
        @Query("name") name: String? = null
    ): Response<List<SportsResponseItem>>

    @GET("/features")
    suspend fun getFeatures(): Response<List<FeatureItem>>

    @GET("/sports/{sport_id}/trainers")
    suspend fun getTrainersBySportId(
        @Path("sport_id") sportId: Int
    ): Response<List<TrainerResponseItem>>

    @GET("/trainers/{trainer_id}/availability")
    suspend fun getTrainerAvailability(
        @Path("trainer_id") trainerId: Int,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<TrainerAvailabilityResponse>

    @GET("/events")
    suspend fun getEvents(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Int? = null,
        @Query("intensities[]") intensities: List<String>? = null,
        @Query("max_price") maxPrice: Int? = null,
        @Query("multi_day") multiDay: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<List<EventDto>>

    @GET("/training_bookings")
    suspend fun getTrainingBookings(): Response<List<com.example.teene.home.data.models.TrainingBookingDto>>

    @POST("/training_bookings")
    suspend fun createTrainingBooking(
        @Body request: TrainingBookingRequest
    ): Response<TrainingBookingResponse>
    
    @POST("/trainers")
    suspend fun createTrainer(
        @Body request: TrainerCreateRequest
    ): Response<TrainerCreateResponse>

    // Images API
    @Multipart
    @POST("/images")
    suspend fun uploadImage(
        @Part("imageable_id") imageableId: RequestBody,
        @Part("imageable_type") imageableType: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<UploadedImageDto>

    @DELETE("/images/{id}")
    suspend fun deleteImage(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("/users/refresh_quickblox_session")
    suspend fun refreshQuickbloxSession(): Response<com.example.teene.domain.models.RefreshQuickbloxSessionResponse>
}