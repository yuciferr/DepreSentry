package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class SaveDailyLLMUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, date: String, dailyLLM: DailyLLM): Result<Boolean> {
        return repository.saveDailyLLM(userId, date, dailyLLM)
    }
} 