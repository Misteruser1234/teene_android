package com.example.tenee.authentication

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Helper object to handle Apple Sign-In flow on Android.
 * 
 * To make this work, the backend needs to provide:
 * 1. CLIENT_ID: The Service ID created in the Apple Developer Portal.
 * 2. REDIRECT_URI: The endpoint on the backend that Apple will POST the authorization results to.
 */
object AppleSignInHelper {
    // TODO: Replace with actual IDs from the Backend
    const val CLIENT_ID = "com.example.tenee.android.service" 
    const val REDIRECT_URI = "https://api.staging.tenee.io/users/apple_callback"
    const val SCOPE = "name email"

    data class AppleAuthResult(
        val appleUserId: String?,
        val identityToken: String?,
        val firstName: String?,
        val lastName: String?,
        val token: String?
    )

    private val _authResult = MutableSharedFlow<AppleAuthResult>()
    val authResult = _authResult.asSharedFlow()

    /**
     * Launches the Apple Sign-In flow in a Chrome Custom Tab.
     */
    fun openAppleSignIn(context: Context) {
        val url = Uri.Builder()
            .scheme("https")
            .authority("appleid.apple.com")
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("response_type", "code id_token")
            .appendQueryParameter("scope", SCOPE)
            .appendQueryParameter("response_mode", "form_post")
            .build()

        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        intent.launchUrl(context, url)
    }

    suspend fun onAuthReceived(
        appleUserId: String?,
        identityToken: String?,
        firstName: String?,
        lastName: String?,
        token: String?
    ) {
        _authResult.emit(
            AppleAuthResult(
                appleUserId = appleUserId,
                identityToken = identityToken,
                firstName = firstName,
                lastName = lastName,
                token = token
            )
        )
    }
}
