package com.example.depresentry.domain.repository

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.PHQ9Result

interface UserDataRepository {
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
} 