package com.example.teene.domain.models

/**
 * Created by 3100lari on 2025/01/26
 */
data class UserRequest(
    val age: Int? = null,
    val email: String,
    val fullName: String? = null,
    val gender: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val measurementSystem: String = "metric",
    val password: String,
    val phoneNumber: String? = null
)