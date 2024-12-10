package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class ClearAllLocalDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String) {
        repository.clearAllLocalDailyData(userId)
    }
} 