package com.example.tenee.domain.models

data class SocialAuthRequest(
    val token: String? = null,
    val provider: String,
    val fullName: String? = null,
    val email: String? = null,
    val appleUserId: String? = null,
    val googleUserId: String? = null,
    val identityToken: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)
