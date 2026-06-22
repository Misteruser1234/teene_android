package com.example.teene.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Response for POST /users/refresh_quickblox_session.
 * Server returns the same authorization object that contains the refreshed
 * Quickblox session token (and the regular auth token).
 */
data class RefreshQuickbloxSessionResponse(
    val authorization: UsersCreateAuthorization,
    @SerializedName("quickblox_id") val quickbloxId: Int? = null
)
