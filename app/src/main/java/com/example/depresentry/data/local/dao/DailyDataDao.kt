package com.example.depresentry.data.local.dao

import androidx.room.*
import com.example.depresentry.data.local.entity.DailyDataEntity
import java.time.LocalDate
@Dao
interface DailyDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyData(dailyData: DailyDataEntity)

    @Query("SELECT * FROM daily_data WHERE userId = :userId AND id = 'current_data'")
    suspend fun getCurrentDailyData(userId: String): DailyDataEntity?

    @Query("DELETE FROM daily_data WHERE userId = :userId")
    suspend fun clearAllDailyData(userId: String)

    // Mood güncelleme
    @Query("""
        UPDATE daily_data 
        SET mood = :mood, date = :date, timestamp = :timestamp
        WHERE userId = :userId AND id = 'current_data'
    """)
    suspend fun updateMood(
        userId: String, 
        mood: Int, 
        date: LocalDate = LocalDate.now(),
        timestamp: Long = System.currentTimeMillis()
    )

    // PHQ9 güncelleme
    @Query("""
        UPDATE daily_data 
        SET phq9Score = :score, phq9Answers = :answers, date = :date, timestamp = :timestamp
        WHERE userId = :userId AND id = 'current_data'
    """)
    suspend fun updatePHQ9(
        userId: String, 
        score: Int, 
        answers: List<Int>,
        date: LocalDate = LocalDate.now(),
        timestamp: Long = System.currentTimeMillis()
    )

    // Sleep verilerini güncelleme
    @Query("""
        UPDATE daily_data 
        SET sleepDuration = :duration, 
            sleepQuality = :quality, 
            sleepStartTime = :startTime, 
            sleepEndTime = :endTime,
            date = :date,
            timestamp = :timestamp
        WHERE userId = :userId AND id = 'current_data'
    """)
    suspend fun updateSleep(
        userId: String,
        duration: Double,
        quality: String,
        startTime: String,
        endTime: String,
        date: LocalDate = LocalDate.now(),
        timestamp: Long = System.currentTimeMillis()
    )

    // Steps ve aktivite verilerini güncelleme
    @Query("""
        UPDATE daily_data 
        SET steps = :steps,
            isLeavedHome = :isLeavedHome,
            burnedCalorie = :burnedCalorie,
            date = :date,
            timestamp = :timestamp
        WHERE userId = :userId AND id = 'current_data'
    """)
    suspend fun updateActivity(
        userId: String,
        steps: Int,
        isLeavedHome: Boolean,
        burnedCalorie: Int,
        date: LocalDate = LocalDate.now(),
        timestamp: Long = System.currentTimeMillis()
    )

    // Screen time güncelleme
    @Query("""
        UPDATE daily_data 
        SET screenTimeTotal = :total,
            screenTimeByApp = :byApp,
            date = :date,
            timestamp = :timestamp
        WHERE userId = :userId AND id = 'current_data'
    """)
    suspend fun updateScreenTime(
        userId: String,
        total: Double,
        byApp: Map<String, Double>,
        date: LocalDate = LocalDate.now(),
        timestamp: Long = System.currentTimeMillis()
    )
} 