package com.example.tenee.profile.presentation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tenee.data.TokenManager
import kotlinx.coroutines.launch

/**
 * Created by 3100lari on 2025/02/17
 */
class ProfileViewModel(private val tokenManager: TokenManager) : ViewModel()
{

    fun logout()
    {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}