package com.example.teene.domain.models

/**
 * Created by 3100lari on 2025/02/09
 */
data class AuthorizeResponse(
    val address: String? = null,
    val age: String? = null,
    val authorization: UsersCreateAuthorization,
    val email: String,
    val fullName: String,
    val gender: Any,
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val measurementSystem: String,
    val phoneNumber: String? = null,
    val physicalPreparation: String? = null,
    @com.google.gson.annotations.SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)
