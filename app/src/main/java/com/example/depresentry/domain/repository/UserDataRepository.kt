package com.example.depresentry.domain.repository

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.PHQ9Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserDataRepository {
    // Remote operations
    suspend fun saveDailyData(userId: String, date: String, dailyData: DailyData): Result<Boolean>
    suspend fun getDailyData(userId: String, date: String): Result<DailyData?>
    suspend fun getWeeklyData(userId: String, startDate: String, endDate: String): Result<List<DailyData>>
    suspend fun getMonthlyData(userId: String, yearMonth: String): Result<List<DailyData>>
    
    suspend fun savePHQ9Result(userId: String, phq9Result: PHQ9Result): Result<Boolean>
    suspend fun getPHQ9Results(userId: String): Result<List<PHQ9Result>>
    
    suspend fun saveDailyLLM(userId: String, date: String, dailyLLM: DailyLLM): Result<Boolean>
    suspend fun getDailyLLM(userId: String, date: String): Result<DailyLLM?>
    suspend fun getWeeklyLLM(userId: String, startDate: String, endDate: String): Result<List<DailyLLM>>
    suspend fun getMonthlyLLM(userId: String, yearMonth: String): Result<List<DailyLLM>>

    // Local Chat Message operations
    suspend fun insertLocalChatMessage(message: ChatMessageEntity)
    fun getLocalChatHistory(userId: String): Flow<List<ChatMessageEntity>>
    fun getLocalChatHistoryByDate(userId: String, date: LocalDate): Flow<List<ChatMessageEntity>>
    suspend fun clearLocalChatHistory(userId: String)
    suspend fun getLastLocalMessageByType(userId: String, messageType: String): ChatMessageEntity?

    // Local Daily Data operations
    suspend fun insertLocalDailyData(dailyData: DailyDataEntity)
    suspend fun getLocalDailyDataByDate(userId: String, date: LocalDate): DailyDataEntity?
    fun getAllLocalDailyData(userId: String): Flow<List<DailyDataEntity>>
    suspend fun clearAllLocalDailyData(userId: String)
} 