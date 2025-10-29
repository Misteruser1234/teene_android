package com.example.teene.ui.viewModel

import com.example.teene.domain.models.AuthorizeResponse

/**
 * Created by 3100lari on 2025/02/09
 */
sealed class LoginUIState
{
    data object Initial : LoginUIState() // Represents the initial state (before any interaction).
    data object Loading : LoginUIState() // Represents the loading state during login.
    data class Success(val authorizeResponse: AuthorizeResponse?) :
        LoginUIState() // Represents a successful login.

    data class Error(val exception: Throwable?) : LoginUIState() // Represents an error during
// login.
}
