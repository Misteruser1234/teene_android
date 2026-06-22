package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.TokenManager
import com.example.teene.domain.models.AuthorizeRequest
import com.example.teene.domain.usecases.LoginUseCase
import com.example.teene.inbox.data.QuickbloxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/02/09
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val userDataStore: com.example.teene.data.UserDataStore,
    private val quickbloxManager: QuickbloxManager
) : ViewModel()
{
    private val _userLoginState =
        MutableStateFlow<LoginUIState>(LoginUIState.Initial)
    val userLoginState: StateFlow<LoginUIState> get() = _userLoginState

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
                    if (result.isSuccess)
                    {
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
                    if (result.isFailure)
                    {
                        _userLoginState.emit(LoginUIState.Error(result.exceptionOrNull()))

                    }
                }
        }
    }

    fun resetLoginState() {
        viewModelScope.launch { _userLoginState.emit(LoginUIState.Initial) }
    }
}