package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetCurrentDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String): DailyDataEntity? {
        return repository.getCurrentDailyData(userId)
    }
} 