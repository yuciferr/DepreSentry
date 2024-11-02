package com.example.depresentry.domain.usecase.auth

import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        return userRepository.resetPassword(email)
    }
}
