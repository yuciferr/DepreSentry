package com.example.depresentry.domain.calculator

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.UserProfile

interface DepressionScoreCalculator {
    fun calculateScore(dailyData: DailyData, userProfile: UserProfile): Double
} 