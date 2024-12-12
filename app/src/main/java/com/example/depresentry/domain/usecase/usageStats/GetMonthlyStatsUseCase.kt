package com.example.depresentry.domain.usecase.usageStats

import com.example.depresentry.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetMonthlyStatsUseCase @Inject constructor(
    private val repository: UsageStatsRepository
) {
    operator fun invoke(): Map<String, Long> {
        return repository.getMonthlyStats()
    }
} 