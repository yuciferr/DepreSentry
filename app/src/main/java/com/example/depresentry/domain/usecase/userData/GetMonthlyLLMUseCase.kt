package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetMonthlyLLMUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, yearMonth: String): Result<List<DailyLLM>> {
        return repository.getMonthlyLLM(userId, yearMonth)
    }
} 