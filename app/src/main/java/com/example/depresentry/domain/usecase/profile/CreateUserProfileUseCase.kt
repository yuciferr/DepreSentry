package com.example.depresentry.domain.usecase.profile


import android.util.Log
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class CreateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Boolean> {
        return userRepository.createUserProfile(userProfile)
    }
}
