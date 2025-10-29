package com.example.teene.domain.usecases

import UsersRepository
import com.example.teene.domain.models.AuthorizeRequest
import com.example.teene.domain.models.AuthorizeResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by 3100lari on 2025/02/09
 */
class LoginUseCase(private val userRepository: UsersRepository) {

    fun execute(loginRequest: AuthorizeRequest): Flow<Result<AuthorizeResponse>>
    {
        return userRepository.loginUser(loginRequest)  // Returns Flow from the repository
    }
}