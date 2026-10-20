package com.example.tenee.domain.models

import com.google.gson.annotations.SerializedName

data class UsersCreateResponse(
    val address: String? = null,
    val age: Int? = null,
    val authorization: UsersCreateAuthorization,
    val email: String,
    @SerializedName("full_name") val fullName: String?=null,
    val gender: String? = null,
    val id: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("measurement_system") val measurementSystem: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("physical_preparation") val physicalPreparation: String? = null,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)