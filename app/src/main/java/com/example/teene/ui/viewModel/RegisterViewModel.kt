package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.TokenManager
import com.example.teene.data.UserDataStore
import com.example.teene.domain.models.UserRequest
import com.example.teene.domain.models.UsersCreateResponse
import com.example.teene.domain.usecases.CreateUserUseCase
import com.example.teene.inbox.data.QuickbloxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/01/26
 */
class RegisterViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val tokenManager: TokenManager,
    private val userDataStore: UserDataStore,
    private val quickbloxManager: QuickbloxManager
) : ViewModel()
{
    // src/main/java/com/example/teene/ui/viewModel/RegisterViewModel.kt
    private val _uiState = MutableStateFlow(RegisterUserUiState())
    val uiState: StateFlow<RegisterUserUiState> get() = _uiState

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
    }}