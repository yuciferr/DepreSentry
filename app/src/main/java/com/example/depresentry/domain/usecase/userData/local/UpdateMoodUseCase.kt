package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UpdateMoodUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, mood: Int) {
        repository.updateMood(userId, mood)
    }
} 