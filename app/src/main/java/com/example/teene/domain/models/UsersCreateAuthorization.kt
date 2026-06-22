package com.example.teene.domain.models

/**
 * Created by 3100lari on 2025/01/26
 */
import com.google.gson.annotations.SerializedName

data class UsersCreateAuthorization(
    val token: String,
    @SerializedName("quickblox_session_token") val quickbloxSessionToken: String? = null
)