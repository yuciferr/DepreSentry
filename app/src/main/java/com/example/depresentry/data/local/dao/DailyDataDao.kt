package com.example.depresentry.data.local.dao

import androidx.room.*
import com.example.depresentry.data.local.entity.DailyDataEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyData(dailyData: DailyDataEntity)

    @Query("SELECT * FROM daily_data WHERE userId = :userId AND date = :date")
    suspend fun getDailyDataByDate(userId: String, date: LocalDate): DailyDataEntity?

    @Query("SELECT * FROM daily_data WHERE userId = :userId ORDER BY date DESC")
    fun getAllDailyData(userId: String): Flow<List<DailyDataEntity>>

    @Query("DELETE FROM daily_data WHERE userId = :userId")
    suspend fun clearAllDailyData(userId: String)
} 