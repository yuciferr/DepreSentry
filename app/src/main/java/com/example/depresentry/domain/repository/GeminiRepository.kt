package com.example.depresentry.domain.repository

import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.model.Task
import com.example.depresentry.domain.model.UserProfile

interface GeminiRepository {
    suspend fun processUserProfile(userProfile: UserProfile): Result<Boolean>
    suspend fun processDailyData(dailyData: DailyData): Result<Boolean>
    suspend fun generateWelcomeMessage(): Result<String>
    suspend fun generateAffirmationMessage(): Result<String>
    suspend fun generateDailyTodos(): Result<List<Task>>
    suspend fun generateNotifications(): Result<List<Notification>>
    fun resetChat()
} 