package com.example.depresentry.domain.usecase.usageStats

import com.example.depresentry.domain.repository.UsageStatsRepository
import javax.inject.Inject

class FormatDurationUseCase @Inject constructor(
    private val repository: UsageStatsRepository
) {
    operator fun invoke(millis: Long): String {
        return repository.formatDuration(millis)
    }
} 