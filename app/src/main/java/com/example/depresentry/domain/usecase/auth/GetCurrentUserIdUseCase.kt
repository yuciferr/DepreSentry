package com.example.depresentry.domain.usecase.auth

import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.getCurrentUserId()
}