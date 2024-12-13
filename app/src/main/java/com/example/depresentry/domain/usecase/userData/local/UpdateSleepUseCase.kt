package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UpdateSleepUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(
        userId: String,
        duration: Double,
        quality: String,
        startTime: String,
        endTime: String
    ) {
        repository.updateSleep(userId, duration, quality, startTime, endTime)
    }
} 