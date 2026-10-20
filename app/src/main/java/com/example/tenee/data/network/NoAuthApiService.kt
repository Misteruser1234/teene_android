package com.example.tenee.data.network

import com.example.tenee.domain.models.AuthorizeRequest
import com.example.tenee.domain.models.AuthorizeResponse
import com.example.tenee.domain.models.ForgotPasswordRequest
import com.example.tenee.domain.models.SocialAuthRequest
import com.example.tenee.domain.models.UserRequest
import com.example.tenee.domain.models.UsersCreateResponse
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

    @POST("users/social_authenticate")
    suspend fun authorizeSocial(@Body request: SocialAuthRequest): Response<AuthorizeResponse>

    @POST("users/forgot_password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

}