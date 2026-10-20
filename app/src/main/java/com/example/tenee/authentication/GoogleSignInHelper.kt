package com.example.tenee.authentication

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Helper object to handle native Google Sign-In on Android using the
 * Credential Manager + "Sign in with Google" (googleid) libraries.
 *
 * This uses the on-device account picker (no browser / Chrome Custom Tab).
 * The value sent to the backend is the Google ID token ([GoogleAuthResult.idToken]).
 *
 * To make this work you need, from the Google Cloud project owner:
 * 1. [WEB_CLIENT_ID]: the Web (server) OAuth Client ID used as the serverClientId.
 *    The backend must verify that the ID token's `aud` equals this Web Client ID.
 * 2. An Android OAuth Client ID registered with the app package name and the
 *    SHA-1 fingerprint(s) of the signing keystore(s) (debug, release, Play signing).
 */
object GoogleSignInHelper {
    // TODO: Replace with the real Web (server) OAuth Client ID from Google Cloud Console.
    const val WEB_CLIENT_ID = "564063800923-3a9cdq4gq5kjlkv0nr225r5nck091bpp.apps.googleusercontent.com"

    data class GoogleAuthResult(
        val idToken: String,
        val googleUserId: String?,
        val fullName: String?,
        val firstName: String?,
        val lastName: String?,
        val email: String?
    )

    private val _authResult = MutableSharedFlow<GoogleAuthResult>()
    val authResult = _authResult.asSharedFlow()

    private val _authError = MutableSharedFlow<String>()
    val authError = _authError.asSharedFlow()

    /**
     * Launches the native Google Sign-In flow via Credential Manager.
     *
     * [context] should be an Activity context. On success a [GoogleAuthResult]
     * (containing the ID token to send to the backend) is emitted on [authResult];
     * on failure an error message is emitted on [authError].
     *
     * `setFilterByAuthorizedAccounts(false)` allows sign-up with any account on the
     * device, not only accounts that previously authorized the app.
     */
    suspend fun signIn(context: Context) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val response = credentialManager.getCredential(context, request)
            val credential = response.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            _authResult.emit(
                GoogleAuthResult(
                    idToken = googleIdTokenCredential.idToken,
                    // The Google User ID is the `sub` claim inside the ID token JWT.
                    googleUserId = extractSubject(googleIdTokenCredential.idToken),
                    fullName = googleIdTokenCredential.displayName,
                    firstName = googleIdTokenCredential.givenName,
                    lastName = googleIdTokenCredential.familyName,
                    email = googleIdTokenCredential.id
                )
            )
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInHelper", "Google sign-in failed", e)
            _authError.emit(e.message ?: "Google sign-in failed")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("GoogleSignInHelper", "Invalid Google ID token", e)
            _authError.emit(e.message ?: "Invalid Google ID token")
        }
    }

    /**
     * Extracts the `sub` (subject) claim from a JWT ID token. For Google this is the
     * stable Google User ID sent to the backend as `google_user_id`.
     */
    private fun extractSubject(idToken: String): String? {
        return try {
            val parts = idToken.split(".")
            if (parts.size < 2) return null
            val payload = String(
                android.util.Base64.decode(
                    parts[1],
                    android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
                )
            )
            org.json.JSONObject(payload).optString("sub").takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.w("GoogleSignInHelper", "Could not extract subject from ID token", e)
            null
        }
    }
}
