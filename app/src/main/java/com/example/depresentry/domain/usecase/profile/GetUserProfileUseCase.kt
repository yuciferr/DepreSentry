package com.example.depresentry.domain.usecase.profile

import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<UserProfile?> {
        return userRepository.getUserProfile(userId)
    }
}
