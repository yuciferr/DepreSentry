package com.example.depresentry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_data")
data class DailyDataEntity(
    @PrimaryKey
    val id: String = "current_data",
    val date: LocalDate = LocalDate.now(),
    val userId: String,
    val depressionScore: Int? = null,
    val steps: Int? = null,
    val isLeavedHome: Boolean? = null,
    val burnedCalorie: Int? = null,
    val sleepDuration: Double? = null,
    val sleepQuality: String? = null,
    val sleepStartTime: String? = null,
    val sleepEndTime: String? = null,
    val mood: Int? = null,
    val screenTimeTotal: Double? = null,
    val screenTimeByApp: Map<String, Double>? = null,
    val phq9Score: Int? = null,
    val phq9Answers: List<Int>? = null,
    val timestamp: Long = System.currentTimeMillis()
) 