package com.example.teene.domain.usecases


import UsersRepository
import com.example.teene.domain.models.UserRequest
import com.example.teene.domain.models.UsersCreateResponse
import kotlinx.coroutines.flow.Flow

class CreateUserUseCase(private val userRepository: UsersRepository) {
     fun execute(userRequest: UserRequest): Flow<Result<UsersCreateResponse>> {
        return userRepository.createUser(userRequest)  // Returns Flow from the repository
    }
}
