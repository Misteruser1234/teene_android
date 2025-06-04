package com.example.teene.data.network

import com.example.teene.home.data.models.SportsResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by 3100lari on 2025/05/29
 */
interface AuthorizedApiService
{

     @GET("/sports")
     suspend fun getSports(): Response<SportsResponse>

}