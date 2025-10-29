package com.example.teene.ui.viewModel

/**
 * Created by 3100lari on 2025/08/07
 */
sealed class ForgotPasswordUIState {
    object Initial : ForgotPasswordUIState()
    object Loading : ForgotPasswordUIState()
    data class Success(val data: Any?) : ForgotPasswordUIState()
    data class Error(val error: Throwable?) : ForgotPasswordUIState()
}