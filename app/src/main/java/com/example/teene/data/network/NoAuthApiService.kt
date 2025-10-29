package com.example.teene.data.network

import com.example.teene.domain.models.AuthorizeRequest
import com.example.teene.domain.models.AuthorizeResponse
import com.example.teene.domain.models.ForgotPasswordRequest
import com.example.teene.domain.models.UserRequest
import com.example.teene.domain.models.UsersCreateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by 3100lari on 2025/01/26
 */
interface NoAuthApiService
{
    @POST("users")
    suspend fun createUser(@Body user: UserRequest): Response<UsersCreateResponse>

    @POST("users/authenticate")
    suspend fun authorize(@Body user: AuthorizeRequest): Response<AuthorizeResponse>

    @POST("users/forgot_password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

}