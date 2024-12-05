package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class SaveDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, date: String, dailyData: DailyData): Result<Boolean> {
        return repository.saveDailyData(userId, date, dailyData)
    }
} 