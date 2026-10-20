package com.example.tenee.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tenee.data.TokenManager
import com.example.tenee.domain.models.AuthorizeRequest
import com.example.tenee.domain.models.SocialAuthRequest
import com.example.tenee.domain.usecases.LoginUseCase
import com.example.tenee.domain.usecases.SocialLoginUseCase
import com.example.tenee.inbox.data.QuickbloxManager
import com.example.tenee.authentication.AppleSignInHelper
import com.example.tenee.authentication.GoogleSignInHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/02/09
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val tokenManager: TokenManager,
    private val userDataStore: com.example.tenee.data.UserDataStore,
    private val quickbloxManager: QuickbloxManager
) : ViewModel()
{
    private val _userLoginState =
        MutableStateFlow<LoginUIState>(LoginUIState.Initial)
    val userLoginState: StateFlow<LoginUIState> get() = _userLoginState

    init {
        viewModelScope.launch {
            AppleSignInHelper.authResult.collect { result ->
                loginWithApple(result)
            }
        }
        viewModelScope.launch {
            GoogleSignInHelper.authResult.collect { result ->
                loginWithGoogle(result)
            }
        }
        viewModelScope.launch {
            GoogleSignInHelper.authError.collect { message ->
                _userLoginState.emit(LoginUIState.Error(Exception(message)))
            }
        }
    }

    fun loginUser(emailInput: String, passwordInput: String)
    {
        viewModelScope.launch {
            _userLoginState.emit(LoginUIState.Loading)
            loginUseCase.execute(
                AuthorizeRequest(
                    email = emailInput,
                    password = passwordInput
                )
            )
                .collect { result ->
                    handleAuthResult(result)
                }
        }
    }

    fun loginWithApple(result: AppleSignInHelper.AppleAuthResult) {
        viewModelScope.launch {
            _userLoginState.emit(LoginUIState.Loading)
            socialLoginUseCase.execute(
                SocialAuthRequest(
                    token = result.token,
                    provider = "apple",
                    appleUserId = result.appleUserId,
                    identityToken = result.identityToken,
                    firstName = result.firstName,
                    lastName = result.lastName
                )
            ).collect { res ->
                handleAuthResult(res)
            }
        }
    }

    fun loginWithGoogle(result: GoogleSignInHelper.GoogleAuthResult) {
        viewModelScope.launch {
            _userLoginState.emit(LoginUIState.Loading)
            socialLoginUseCase.execute(
                SocialAuthRequest(
                    provider = "google",
                    identityToken = result.idToken,
                    googleUserId = result.googleUserId,
                    email = result.email,
                    fullName = result.fullName,
                    firstName = result.firstName,
                    lastName = result.lastName
                )
            ).collect { res ->
                handleAuthResult(res)
            }
        }
    }

    private suspend fun handleAuthResult(result: Result<com.example.tenee.domain.models.AuthorizeResponse>) {
        if (result.isSuccess) {
            _userLoginState.emit(LoginUIState.Success(result.getOrNull()))
            val data = result.getOrNull()
            data?.authorization?.token?.let { token ->
                tokenManager.saveToken(token)
            }
            // Persist user_id for authorized sessions (used by bookings)
            data?.id?.let { id ->
                userDataStore.saveUserId(id)
            }
            // Persist QuickBlox session info and restore SDK session.
            val qbToken = data?.authorization?.quickbloxSessionToken
            val qbId = data?.quickbloxId
            if (qbToken != null && qbId != null) {
                userDataStore.saveQuickbloxSessionToken(qbToken)
                userDataStore.saveQuickbloxId(qbId)
                quickbloxManager.restoreSession(qbToken, qbId)
                quickbloxManager.connectChat()
            }
        }
        if (result.isFailure) {
            _userLoginState.emit(LoginUIState.Error(result.exceptionOrNull()))
        }
    }

    fun resetLoginState() {
        viewModelScope.launch { _userLoginState.emit(LoginUIState.Initial) }
    }
}