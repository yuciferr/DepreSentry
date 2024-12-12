package com.example.depresentry.domain.model

data class DailyData(
    val date: String = "",
    val depressionScore: Int = 0,
    val mood: Int = 0,
    val steps: Steps = Steps(),
    val sleep: Sleep = Sleep(),
    val screenTime: ScreenTime = ScreenTime()
)

data class Steps(
    val steps: Int = 0,
    val isLeavedHome: Boolean = false,
    val burnedCalorie: Int = 0
)

data class Sleep(
    val duration: Double = 0.0,
    val quality: String = "",
    val sleepStartTime: String = "",
    val sleepEndTime: String = ""
)

data class ScreenTime(
    val total: Double = 0.0,
    val byApp: Map<String, Double> = emptyMap()
) 