package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetWeeklyLLMUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, startDate: String, endDate: String): Result<List<DailyLLM>> {
        return repository.getWeeklyLLM(userId, startDate, endDate)
    }
} 