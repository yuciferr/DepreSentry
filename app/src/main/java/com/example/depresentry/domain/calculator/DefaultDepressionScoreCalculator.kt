package com.example.depresentry.domain.calculator

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.UserProfile
import kotlin.math.max
import kotlin.math.min

class DefaultDepressionScoreCalculator : DepressionScoreCalculator {
    override fun calculateScore(dailyData: DailyData, userProfile: UserProfile): Double {
        val physicalActivityScore = calculatePhysicalActivityScore(dailyData)
        val sleepScore = calculateSleepScore(dailyData)
        val emotionalScore = calculateEmotionalScore(dailyData)
        val digitalHabitsScore = calculateDigitalHabitsScore(dailyData)
        val profileMultiplier = calculateProfileMultiplier(userProfile)

        val baseScore = (
            (physicalActivityScore * 0.35) +
            (sleepScore * 0.25) +
            (emotionalScore * 0.30) +
            (digitalHabitsScore * 0.10)
        ) * 100

        return normalizeScore(baseScore + (baseScore * profileMultiplier))
    }

    private fun calculatePhysicalActivityScore(dailyData: DailyData): Double {
        val stepsScore = when {
            dailyData.steps.steps >= 10000 -> 1.0
            dailyData.steps.steps >= 5000 -> 0.7
            else -> 0.3
        } * 0.60

        val homeScore = if (dailyData.steps.isLeavedHome) 1.0 else 0.3
        return stepsScore + (homeScore * 0.40)
    }

    private fun calculateSleepScore(dailyData: DailyData): Double {
        val durationHours = dailyData.sleep.duration
        val durationScore = when {
            durationHours in 7.0..9.0 -> 1.0
            durationHours in 5.0..7.0 || durationHours in 9.0..10.0 -> 0.6
            else -> 0.2
        } * 0.50

        val qualityScore = when (dailyData.sleep.quality.lowercase()) {
            "good" -> 1.0
            "medium" -> 0.6
            else -> 0.2
        } * 0.30

        val timeScore = calculateSleepTimeScore(dailyData.sleep.sleepStartTime, dailyData.sleep.sleepEndTime) * 0.20
        
        return durationScore + qualityScore + timeScore
    }

    private fun calculateSleepTimeScore(startTime: String, endTime: String): Double {
        // Basit bir kontrol için saat değerlerini alıyoruz
        val startHour = startTime.split(":")[0].toInt()
        val endHour = endTime.split(":")[0].toInt()

        return when {
            startHour in 22..23 && endHour in 5..6 -> 1.0
            startHour in 21..22 || endHour in 6..7 -> 0.6
            else -> 0.2
        }
    }

    private fun calculateEmotionalScore(dailyData: DailyData): Double {
        val moodScore = when (dailyData.mood) {
            in 8..10 -> 1.0
            in 5..7 -> 0.6
            else -> 0.2
        } * 0.40

        val phqScore = when (dailyData.depressionScore) {
            in 0..4 -> 1.0
            in 5..9 -> 0.5
            else -> 0.1
        } * 0.60

        return moodScore + phqScore
    }

    private fun calculateDigitalHabitsScore(dailyData: DailyData): Double {
        val screenTimeScore = when {
            dailyData.screenTime.total < 2.0 -> 1.0
            dailyData.screenTime.total < 4.0 -> 0.6
            else -> 0.2
        } * 0.60

        // Basit bir app kullanım skoru hesaplama
        val appScore = calculateAppUsageScore(dailyData.screenTime.byApp) * 0.40

        return screenTimeScore + appScore
    }

    private fun calculateAppUsageScore(appUsage: Map<String, Double>): Double {
        // Basit bir sınıflandırma örneği
        val productiveApps = setOf("calendar", "notes", "fitness", "meditation")
        val addictiveApps = setOf("social", "games", "entertainment")

        val totalTime = appUsage.values.sum()
        val productiveTime = appUsage.filter { it.key in productiveApps }.values.sum()
        val addictiveTime = appUsage.filter { it.key in addictiveApps }.values.sum()

        return when {
            productiveTime > addictiveTime -> 1.0
            productiveTime == addictiveTime -> 0.6
            else -> 0.2
        }
    }

    private fun calculateProfileMultiplier(profile: UserProfile): Double {
        var multiplier = 0.0

        // Cinsiyet çarpanı
        multiplier += when (profile.gender?.lowercase()) {
            "Male" -> 0.0
            "Female" -> 0.1
            "Other" -> 0.05
            else -> 0.0
        }

        // Yaş çarpanı
        profile.age?.let { age ->
            multiplier += when {
                age in 18..25 -> 0.2
                age > 40 -> 0.3
                else -> 0.0
            }
        }

        // Medeni durum çarpanı
        multiplier += when (profile.maritalStatus?.lowercase()) {
            "Single" -> 0.1
            "In Relationship" -> 0.1
            "Married" -> -0.2
            "Divorced" -> 0.3
            "Widowed" -> 0.2
            else -> 0.0
        }

        // Meslek çarpanı
        multiplier += when (profile.profession?.lowercase()) {
            "Unemployed" -> 0.3
            "Heavy Work Schedule" -> 0.2
            "Balanced Work" -> 0.0
            else -> 0.0
        }

        // Ülke çarpanı
        multiplier += getCountryMultiplier(profile.country)

        return multiplier
    }

    private fun getCountryMultiplier(country: String?): Double {
        return when (country) {
            "Western Europe", "North America" -> -0.1
            "Central and Eastern Europe" -> 0.0
            "Latin America", "Middle East", "North Africa" -> 0.1
            "South Asia" -> 0.15
            "East Asia" -> 0.05
            "Southeast Asia" -> 0.1
            "Sub Saharan Africa", "Central Asia" -> 0.2
            else -> 0.0
        }
    }

    private fun normalizeScore(score: Double): Double {
        return max(0.0, min(100.0, score))
    }
} 