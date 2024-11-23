package com.example.depresentry.domain.usecase.profile

import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class SaveLocalProfileImageUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, imagePath: String): Result<Boolean> {
        return repository.saveProfileImageLocally(userId, imagePath)
    }
} 