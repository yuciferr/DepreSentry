package com.example.depresentry.domain.usecase.auth

import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return userRepository.deleteUserAccount()
    }
}
