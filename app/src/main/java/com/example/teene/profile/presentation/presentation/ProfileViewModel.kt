package com.example.teene.profile.presentation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.data.TokenManager
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