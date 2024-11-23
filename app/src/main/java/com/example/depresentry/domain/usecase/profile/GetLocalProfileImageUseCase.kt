package com.example.depresentry.domain.usecase.profile

import com.example.depresentry.domain.repository.UserRepository
import javax.inject.Inject

class GetLocalProfileImageUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<String?> {
        return repository.getLocalProfileImage(userId)
    }
} 