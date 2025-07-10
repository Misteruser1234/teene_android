package com.example.teene.data.network

import com.example.teene.home.data.models.SportsResponseItem
import com.example.teene.home.data.models.TrainerResponseItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by 3100lari on 2025/05/29
 */
interface AuthorizedApiService
{

    @GET("/sports")
    suspend fun getSports(): Response<List<SportsResponseItem>>

    @GET("/sports/{sport_id}/trainers")
    suspend fun getTrainersBySportId(
        @Path("sport_id") sportId: Int
    ): Response<List<TrainerResponseItem>>

}