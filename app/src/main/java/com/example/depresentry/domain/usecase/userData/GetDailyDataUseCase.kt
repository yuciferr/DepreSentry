package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, date: String): Result<DailyData?> {
        return repository.getDailyData(userId, date)
    }
} 