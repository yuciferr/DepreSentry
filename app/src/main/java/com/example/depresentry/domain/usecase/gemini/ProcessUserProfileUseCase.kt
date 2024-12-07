package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class ProcessUserProfileUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Boolean> {
        return repository.setUserProfileMessage(userProfile)
    }
} 