package com.example.depresentry.domain.repository

import android.content.Intent

interface UsageStatsRepository {
    fun hasUsageStatsPermission(): Boolean
    fun getUsageStatsSettingsIntent(): Intent
    fun getDailyStats(): Map<String, Long>
    fun getWeeklyStats(): Map<String, Long>
    fun getMonthlyStats(): Map<String, Long>
    fun formatDuration(millis: Long): String
} 