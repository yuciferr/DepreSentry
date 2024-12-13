package com.example.depresentry.data.repository

import com.example.depresentry.data.local.dao.ChatMessageDao
import com.example.depresentry.data.local.dao.DailyDataDao
import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.PHQ9Result
import com.example.depresentry.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val databaseService: FireStoreDatabaseService,
    private val chatMessageDao: ChatMessageDao,
    private val dailyDataDao: DailyDataDao
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

    // Local Chat Message operations
    override suspend fun insertLocalChatMessage(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    override fun getLocalChatHistory(userId: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getChatHistory(userId)
    }

    override suspend fun clearLocalChatHistory(userId: String) {
        chatMessageDao.clearChatHistory(userId)
    }

    // Local Daily Data operations
    override suspend fun insertLocalDailyData(dailyData: DailyDataEntity) {
        dailyDataDao.insertDailyData(dailyData)
    }

    override suspend fun getCurrentDailyData(userId: String): DailyDataEntity? {
        return dailyDataDao.getCurrentDailyData(userId)
    }

    override suspend fun clearAllLocalDailyData(userId: String) {
        dailyDataDao.clearAllDailyData(userId)
    }

    override suspend fun getLocalMessageByDateAndTypeAndRole(
        userId: String,
        date: LocalDate,
        messageType: String,
        role: String
    ): ChatMessageEntity? {
        return chatMessageDao.getMessageByDateAndTypeAndRole(userId, date, messageType, role)
    }

    override suspend fun updateMood(userId: String, mood: Int) {
        val today = LocalDate.now()
        val currentData = dailyDataDao.getCurrentDailyData(userId)
        if (currentData == null) {
            dailyDataDao.insertDailyData(
                DailyDataEntity(
                    userId = userId,
                    date = today,
                    mood = mood
                )
            )
        } else {
            dailyDataDao.updateMood(userId, mood, today)
        }
    }

    override suspend fun updatePHQ9(userId: String, score: Int, answers: List<Int>) {
        val today = LocalDate.now()
        val currentData = dailyDataDao.getCurrentDailyData(userId)
        if (currentData == null) {
            dailyDataDao.insertDailyData(
                DailyDataEntity(
                    userId = userId,
                    date = today,
                    phq9Score = score,
                    phq9Answers = answers
                )
            )
        } else {
            dailyDataDao.updatePHQ9(userId, score, answers, today)
        }
    }

    override suspend fun updateSleep(
        userId: String,
        duration: Double,
        quality: String,
        startTime: String,
        endTime: String
    ) {
        val today = LocalDate.now()
        val currentData = dailyDataDao.getCurrentDailyData(userId)
        if (currentData == null) {
            dailyDataDao.insertDailyData(
                DailyDataEntity(
                    userId = userId,
                    date = today,
                    sleepDuration = duration,
                    sleepQuality = quality,
                    sleepStartTime = startTime,
                    sleepEndTime = endTime
                )
            )
        } else {
            dailyDataDao.updateSleep(userId, duration, quality, startTime, endTime, today)
        }
    }

    override suspend fun updateActivity(
        userId: String,
        steps: Int,
        isLeavedHome: Boolean,
        burnedCalorie: Int
    ) {
        val today = LocalDate.now()
        val currentData = dailyDataDao.getCurrentDailyData(userId)
        if (currentData == null) {
            dailyDataDao.insertDailyData(
                DailyDataEntity(
                    userId = userId,
                    date = today,
                    steps = steps,
                    isLeavedHome = isLeavedHome,
                    burnedCalorie = burnedCalorie
                )
            )
        } else {
            dailyDataDao.updateActivity(userId, steps, isLeavedHome, burnedCalorie, today)
        }
    }

    override suspend fun updateScreenTime(
        userId: String,
        total: Double,
        byApp: Map<String, Double>
    ) {
        val today = LocalDate.now()
        val currentData = dailyDataDao.getCurrentDailyData(userId)
        if (currentData == null) {
            dailyDataDao.insertDailyData(
                DailyDataEntity(
                    userId = userId,
                    date = today,
                    screenTimeTotal = total,
                    screenTimeByApp = byApp
                )
            )
        } else {
            dailyDataDao.updateScreenTime(userId, total, byApp, today)
        }
    }
} 