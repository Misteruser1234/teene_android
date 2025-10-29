package com.example.teene.domain.usecases

import UsersRepository
import com.example.teene.domain.models.ForgotPasswordRequest
import kotlinx.coroutines.flow.Flow

/**
 * Created by 3100lari on 2025/08/07
 */
class ForgotPasswordUseCase(private val userRepository: UsersRepository) {
    fun execute(forgotPasswordRequest: ForgotPasswordRequest): Flow<Result<Unit>> {
        return userRepository.forgotPassword(forgotPasswordRequest)  // Returns Flow from the
    // repository
    }
}