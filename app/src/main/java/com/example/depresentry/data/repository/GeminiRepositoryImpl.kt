package com.example.depresentry.data.repository

import com.example.depresentry.data.remote.api.GeminiAIService
import com.example.depresentry.domain.model.*
import com.example.depresentry.domain.repository.GeminiRepository
import com.google.gson.Gson
import javax.inject.Inject

class GeminiRepositoryImpl @Inject constructor(
    private val geminiService: GeminiAIService
) : GeminiRepository {
    
    private val gson = Gson()

    override suspend fun processUserProfile(userProfile: UserProfile): Result<Boolean> {
        return geminiService.sendUserProfile(gson.toJson(userProfile))
            .map { it == "ok" }
    }

    override suspend fun processDailyData(dailyData: DailyData): Result<Boolean> {
        return geminiService.sendDailyData(gson.toJson(dailyData))
            .map { it == "ok" }
    }

    override suspend fun generateWelcomeMessage(): Result<String> {
        return geminiService.generateWelcomeMessage()
    }

    override suspend fun generateAffirmationMessage(): Result<String> {
        return geminiService.generateAffirmationMessage()
    }

    override suspend fun generateDailyTodos(): Result<List<Task>> {
        return geminiService.generateDailyTodos()
    }

    override suspend fun generateNotifications(): Result<List<Notification>> {
        return geminiService.generateNotificationMessages()
    }

    override fun resetChat() {
        geminiService.resetChat()
    }

    override suspend fun setUserProfileMessage(userProfile: UserProfile): Result<Boolean> {
        return processUserProfile(userProfile)
    }
} 