package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetWeeklyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, startDate: String, endDate: String): Result<List<DailyData>> {
        return repository.getWeeklyData(userId, startDate, endDate)
    }
} 