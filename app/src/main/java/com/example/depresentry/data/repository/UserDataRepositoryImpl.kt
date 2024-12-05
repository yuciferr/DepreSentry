package com.example.depresentry.data.repository

import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.PHQ9Result
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val databaseService: FireStoreDatabaseService
) : UserDataRepository {
    
    override suspend fun saveDailyData(userId: String, date: String, dailyData: DailyData): Result<Boolean> {
        return databaseService.saveDailyData(userId, date, dailyData)
    }

    override suspend fun getDailyData(userId: String, date: String): Result<DailyData?> {
        return databaseService.getDailyData(userId, date)
    }

    override suspend fun getWeeklyData(userId: String, startDate: String, endDate: String): Result<List<DailyData>> {
        return databaseService.getWeeklyData(userId, startDate, endDate)
    }

    override suspend fun getMonthlyData(userId: String, yearMonth: String): Result<List<DailyData>> {
        return databaseService.getMonthlyData(userId, yearMonth)
    }

    override suspend fun savePHQ9Result(userId: String, phq9Result: PHQ9Result): Result<Boolean> {
        return databaseService.savePHQ9Result(userId, phq9Result)
    }

    override suspend fun getPHQ9Results(userId: String): Result<List<PHQ9Result>> {
        return databaseService.getPHQ9Results(userId)
    }

    override suspend fun saveDailyLLM(userId: String, date: String, dailyLLM: DailyLLM): Result<Boolean> {
        return databaseService.saveDailyLLM(userId, date, dailyLLM)
    }

    override suspend fun getDailyLLM(userId: String, date: String): Result<DailyLLM?> {
        return databaseService.getDailyLLM(userId, date)
    }

    override suspend fun getWeeklyLLM(userId: String, startDate: String, endDate: String): Result<List<DailyLLM>> {
        return databaseService.getWeeklyLLM(userId, startDate, endDate)
    }

    override suspend fun getMonthlyLLM(userId: String, yearMonth: String): Result<List<DailyLLM>> {
        return databaseService.getMonthlyLLM(userId, yearMonth)
    }
} 