package com.example.tenee.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Created by 3100lari on 2025/02/09
 */
data class AuthorizeResponse(
    val address: String? = null,
    val age: String? = null,
    val authorization: UsersCreateAuthorization,
    val email: String,
    @SerializedName("full_name") val fullName: String? = null,
    val gender: Any? = null,
    val id: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("measurement_system") val measurementSystem: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("physical_preparation") val physicalPreparation: String? = null,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)
