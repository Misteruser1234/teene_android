package com.example.tenee.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Created by 3100lari on 2025/02/09
 */
data class AuthorizeRequest(
    val email: String? = null,
    val password: String? = null,
    val provider: String = "email",
    @SerializedName("apple_user_id") val appleUserId: String? = null,
    @SerializedName("google_user_id") val googleUserId: String? = null,
    @SerializedName("identity_token") val identityToken: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null
)
