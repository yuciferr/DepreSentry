package com.example.depresentry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_data")
data class DailyDataEntity(
    @PrimaryKey
    val date: LocalDate = LocalDate.now(),
    val userId: String,
    val depressionScore: Int,
    val steps: Int,
    val isLeavedHome: Boolean,
    val burnedCalorie: Int,
    val sleepDuration: Double,
    val sleepQuality: String,
    val sleepStartTime: String,
    val sleepEndTime: String,
    val mood: Int,
    val screenTimeTotal: Double,
    val screenTimeByApp: Map<String, Double>,
    val timestamp: Long = System.currentTimeMillis()
) 