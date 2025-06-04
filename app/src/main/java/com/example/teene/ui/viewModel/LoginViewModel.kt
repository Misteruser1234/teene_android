package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.TokenManager
import com.example.teene.domain.models.AuthorizeRequest
import com.example.teene.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/02/09
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
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
                    }
                    if (result.isFailure)
                    {
                        _userLoginState.emit(LoginUIState.Error(result.exceptionOrNull()))

                    }
                }
        }
    }
}