package com.example.depresentry.domain.usecase.auth

import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userCredential: UserCredentials): Result<Boolean> {
        return userRepository.registerUser(userCredential)
    }
}
