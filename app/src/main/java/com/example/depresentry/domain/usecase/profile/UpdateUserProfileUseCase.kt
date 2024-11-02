package com.example.depresentry.domain.usecase.profile

import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase  @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Boolean> {
        return userRepository.updateUserProfile(userProfile)
    }
}