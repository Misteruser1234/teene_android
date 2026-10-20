package com.example.tenee.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tenee.data.TokenManager
import com.example.tenee.data.UserDataStore
import com.example.tenee.domain.models.SocialAuthRequest
import com.example.tenee.domain.models.UserRequest
import com.example.tenee.domain.models.UsersCreateResponse
import com.example.tenee.domain.usecases.CreateUserUseCase
import com.example.tenee.domain.usecases.SocialLoginUseCase
import com.example.tenee.inbox.data.QuickbloxManager
import com.example.tenee.authentication.AppleSignInHelper
import com.example.tenee.authentication.GoogleSignInHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/01/26
 */
class RegisterViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val tokenManager: TokenManager,
    private val userDataStore: UserDataStore,
    private val quickbloxManager: QuickbloxManager
) : ViewModel()
{
    // src/main/java/com/example/tenee/ui/viewModel/RegisterViewModel.kt
    private val _uiState = MutableStateFlow(RegisterUserUiState())
    val uiState: StateFlow<RegisterUserUiState> get() = _uiState

    // One-shot event emitted (with the account email) when a social sign-up succeeds,
    // so the UI can navigate to the post-signup choice screen just like a regular sign-up.
    private val _socialSignupSuccess = MutableSharedFlow<String>()
    val socialSignupSuccess = _socialSignupSuccess.asSharedFlow()

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
                _uiState.value = RegisterUserUiState(error = message)
            }
        }
    }

    fun createUser(
        emailInput: String,
        passwordInput: String,
        name: String? = null,
        age: Int? = null,
        gender: String? = null,
        phoneNumber: String? = null,
        physicalPreparation: Int = 3
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUserUiState(isLoading = true)
            createUserUseCase.execute(
                UserRequest(
                    email = emailInput,
                    password = passwordInput
                )
            ).collect { result ->
                handleAuthResult(result)
            }
        }
    }

    fun loginWithApple(result: AppleSignInHelper.AppleAuthResult) {
        viewModelScope.launch {
            _uiState.value = RegisterUserUiState(isLoading = true)
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
                // Map AuthorizeResponse to RegisterUserUiState if needed, or just handle as success
                if (res.isSuccess) {
                    val response = res.getOrNull()
                    response?.authorization?.token?.let { tokenManager.saveToken(it) }
                    val qbToken = response?.authorization?.quickbloxSessionToken
                    val qbId = response?.quickbloxId
                    if (qbToken != null && qbId != null) {
                        userDataStore.saveQuickbloxSessionToken(qbToken)
                        userDataStore.saveQuickbloxId(qbId)
                        quickbloxManager.restoreSession(qbToken, qbId)
                        quickbloxManager.connectChat()
                    }
                    // Since it's a social login, it might act as both login and register
                    _uiState.value = RegisterUserUiState(
                        user = UsersCreateResponse(
                            authorization = response!!.authorization,
                            email = response.email,
                            fullName = response.fullName,
                            id = response.id,
                            quickbloxId = response.quickbloxId
                        )
                    )
                } else {
                    _uiState.value = RegisterUserUiState(error = res.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun loginWithGoogle(googleResult: GoogleSignInHelper.GoogleAuthResult) {
        viewModelScope.launch {
            _uiState.value = RegisterUserUiState(isLoading = true)
            socialLoginUseCase.execute(
                SocialAuthRequest(
                    provider = "google",
                    identityToken = googleResult.idToken,
                    googleUserId = googleResult.googleUserId,
                    email = googleResult.email,
                    fullName = googleResult.fullName,
                    firstName = googleResult.firstName,
                    lastName = googleResult.lastName
                )
            ).collect { result ->
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    response?.authorization?.token?.let { tokenManager.saveToken(it) }
                    val qbToken = response?.authorization?.quickbloxSessionToken
                    val qbId = response?.quickbloxId
                    if (qbToken != null && qbId != null) {
                        userDataStore.saveQuickbloxSessionToken(qbToken)
                        userDataStore.saveQuickbloxId(qbId)
                        quickbloxManager.restoreSession(qbToken, qbId)
                        quickbloxManager.connectChat()
                    }
                    _uiState.value = RegisterUserUiState(
                        user = UsersCreateResponse(
                            authorization = response!!.authorization,
                            email = response.email,
                            fullName = response.fullName,
                            id = response.id,
                            quickbloxId = response.quickbloxId
                        )
                    )
                    _socialSignupSuccess.emit(googleResult.email ?: response.email ?: "")
                } else {
                    _uiState.value = RegisterUserUiState(error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private suspend fun handleAuthResult(result: Result<UsersCreateResponse>) {
        if (result.isSuccess) {
            val response = result.getOrNull()
            response?.authorization?.token?.let { tokenManager.saveToken(it) }
            val qbToken = response?.authorization?.quickbloxSessionToken
            val qbId = response?.quickbloxId
            if (qbToken != null && qbId != null) {
                userDataStore.saveQuickbloxSessionToken(qbToken)
                userDataStore.saveQuickbloxId(qbId)
                quickbloxManager.restoreSession(qbToken, qbId)
                quickbloxManager.connectChat()
            }
            _uiState.value = RegisterUserUiState(user = response)
        } else {
            _uiState.value = RegisterUserUiState(error = result.exceptionOrNull()?.message)
        }
    }
}