package com.example.teene.domain.models


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
    val physicalPreparation: String
)