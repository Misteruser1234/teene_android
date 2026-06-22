package com.example.teene.domain.models

import com.google.gson.annotations.SerializedName

data class UsersCreateResponse(
    val address: String,
    val age: Int,
    val authorization: UsersCreateAuthorization,
    val email: String,
    val fullName: String,
    val gender: String,
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val measurementSystem: String,
    val phoneNumber: String,
    val physicalPreparation: String,
    @SerializedName("quickblox_id", alternate = ["quickblox_user_id", "qb_id"]) val quickbloxId: Int? = null
)