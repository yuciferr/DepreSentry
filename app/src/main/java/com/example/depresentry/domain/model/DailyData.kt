package com.example.depresentry.domain.model

data class DailyData(
    val depressionScore: Int,
    val steps: Steps,
    val sleep: Sleep,
    val mood: Int,
    val screenTime: ScreenTime
)

data class Steps(
    val steps: Int,
    val isLeavedHome: Boolean,
    val burnedCalorie: Int
)

data class Sleep(
    val duration: Double,
    val quality: String,
    val sleepStartTime: String,
    val sleepEndTime: String
)

data class ScreenTime(
    val total: Double,
    val byApp: Map<String, Double>
) 