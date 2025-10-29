package com.example.teene.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.domain.models.ForgotPasswordRequest
import com.example.teene.domain.usecases.ForgotPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordUIState>(ForgotPasswordUIState.Initial)
    val forgotPasswordState: StateFlow<ForgotPasswordUIState> get() = _forgotPasswordState

    fun forgotPassword(emailInput: String) {
        viewModelScope.launch {
            _forgotPasswordState.emit(ForgotPasswordUIState.Loading)
            forgotPasswordUseCase.execute(ForgotPasswordRequest(emailInput))
                .collect { result ->
                    if (result.isSuccess) {
                        _forgotPasswordState.emit(ForgotPasswordUIState.Success(result.getOrNull()))
                    }
                    if (result.isFailure) {
                        _forgotPasswordState.emit(ForgotPasswordUIState.Error(result.exceptionOrNull()))
                    }
                }
        }
    }
}
