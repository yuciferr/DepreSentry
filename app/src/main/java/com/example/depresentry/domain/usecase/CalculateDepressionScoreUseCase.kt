package com.example.depresentry.domain.usecase

import com.example.depresentry.domain.calculator.DepressionScoreCalculator
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.UserProfile
import javax.inject.Inject

class CalculateDepressionScoreUseCase @Inject constructor(
    private val calculator: DepressionScoreCalculator
) {
    operator fun invoke(dailyData: DailyData, userProfile: UserProfile): Double {
        return calculator.calculateScore(dailyData, userProfile)
    }
} 