package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class ProcessDailyDataUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(dailyData: DailyData): Result<Boolean> {
        return repository.processDailyData(dailyData)
    }
} 