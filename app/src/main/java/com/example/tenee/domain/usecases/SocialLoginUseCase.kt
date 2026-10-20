package com.example.tenee.domain.usecases

import UsersRepository
import com.example.tenee.domain.models.AuthorizeResponse
import com.example.tenee.domain.models.SocialAuthRequest
import kotlinx.coroutines.flow.Flow

class SocialLoginUseCase(private val userRepository: UsersRepository) {
    fun execute(request: SocialAuthRequest): Flow<Result<AuthorizeResponse>> {
        return userRepository.loginSocial(request)
    }
}
