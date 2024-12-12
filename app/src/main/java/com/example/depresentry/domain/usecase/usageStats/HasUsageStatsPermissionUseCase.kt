package com.example.depresentry.domain.usecase.usageStats

import com.example.depresentry.domain.repository.UsageStatsRepository
import javax.inject.Inject

class HasUsageStatsPermissionUseCase @Inject constructor(
    private val repository: UsageStatsRepository
) {
    operator fun invoke(): Boolean {
        return repository.hasUsageStatsPermission()
    }
} 