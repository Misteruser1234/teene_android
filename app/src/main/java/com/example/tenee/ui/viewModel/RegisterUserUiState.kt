package com.example.tenee.ui.viewModel

import com.example.tenee.domain.models.UsersCreateResponse

/**
 * Created by 3100lari on 2025/08/09
 */
data class RegisterUserUiState(
    val isLoading: Boolean = false,
    val user: UsersCreateResponse? = null,
    val error: String? = null
)