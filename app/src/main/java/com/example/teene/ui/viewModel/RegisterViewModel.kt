package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.TokenManager
import com.example.teene.domain.models.UserRequest
import com.example.teene.domain.models.UsersCreateResponse
import com.example.teene.domain.usecases.CreateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/01/26
 */
class RegisterViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val tokenManager: TokenManager
) : ViewModel()
{
    private val _userCreationState =
        MutableStateFlow<Result<UsersCreateResponse?>>(Result.success(null))
    val userCreationState: StateFlow<Result<UsersCreateResponse?>> get() = _userCreationState

    fun createUser(
        emailInput: String,
        passwordInput: String,
        name: String? = null,
        age: Int? = null,
        gender: String? = null,
        phoneNumber: Int? = null,
        physicalPreparation: Int = 3

    )
    {
        viewModelScope.launch {
            createUserUseCase.execute(
                UserRequest(
                    email = emailInput,
                    password = passwordInput
                )
            )
                .collect { result ->
                    if (result.isSuccess)
                    {
                        result.getOrNull()?.authorization?.token?.let { tokenManager.saveToken(it) }
                        _userCreationState.value = result
                    }
                    else
                    {
                    }
                }
        }
    }
}