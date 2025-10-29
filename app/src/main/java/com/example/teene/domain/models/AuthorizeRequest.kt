package com.example.teene.domain.models

/**
 * Created by 3100lari on 2025/02/09
 */
data class AuthorizeRequest(
    val email: String,
    val password: String,
    val provider: String = "email"
)
