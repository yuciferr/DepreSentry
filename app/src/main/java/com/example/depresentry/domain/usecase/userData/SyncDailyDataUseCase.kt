package com.example.depresentry.domain.usecase.userData

import android.util.Log
import com.example.depresentry.domain.model.*
import com.example.depresentry.domain.usecase.userData.local.GetCurrentDailyDataUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import javax.inject.Inject

class SyncDailyDataUseCase @Inject constructor(
    private val getCurrentDailyDataUseCase: GetCurrentDailyDataUseCase,
    private val getLocalMessageUseCase: GetLocalMessageByDateAndTypeAndRoleUseCase,
    private val saveDailyDataUseCase: SaveDailyDataUseCase,
    private val saveDailyLLMUseCase: SaveDailyLLMUseCase,
    private val savePHQ9ResultUseCase: SavePHQ9ResultUseCase
) {
    private val gson = Gson()

    suspend operator fun invoke(userId: String) {
        try {
            Log.d(TAG, "Senkronizasyon başlatılıyor - userId: $userId")
            
            // 1. Get local daily data
            val localDailyData = getCurrentDailyDataUseCase(userId)
            if (localDailyData == null) {
                Log.w(TAG, "Local daily data bulunamadı")
                return
            }
            
            // 2. Get LLM messages
            val today = LocalDate.now()
            val todayStr = today.toString()
            
            val messages = mutableMapOf<String, String>()
            val tasks = mutableListOf<Task>()
            val notifications = mutableListOf<Notification>()

            // Welcome message
            getLocalMessageUseCase(userId, today, "welcome_response", "model")?.let {
                messages["welcome"] = it.content
            }

            // Affirmation message
            getLocalMessageUseCase(userId, today, "affirmation_response", "model")?.let {
                messages["affirmation"] = it.content
            }

            // Tasks
            getLocalMessageUseCase(userId, today, "todos_response", "model")?.let { message ->
                try {
                    val jsonObject = JsonParser.parseString(message.content).asJsonObject
                    val tasksArray = jsonObject.getAsJsonArray("tasks")
                    val taskListType = object : TypeToken<List<Task>>() {}.type
                    tasks.addAll(gson.fromJson(tasksArray, taskListType))
                    
                    Log.d(TAG, "Tasks başarıyla parse edildi: ${tasks.size} task")
                } catch (e: Exception) {
                    Log.e(TAG, "Tasks parse edilirken hata oluştu", e)
                }
            }

            // Notifications
            getLocalMessageUseCase(userId, today, "notifications_response", "model")?.let { message ->
                try {
                    val jsonObject = JsonParser.parseString(message.content).asJsonObject
                    val notificationsArray = jsonObject.getAsJsonArray("notifications")
                    val notificationListType = object : TypeToken<List<Notification>>() {}.type
                    notifications.addAll(gson.fromJson(notificationsArray, notificationListType))
                    
                    Log.d(TAG, "Notifications başarıyla parse edildi: ${notifications.size} notification")
                } catch (e: Exception) {
                    Log.e(TAG, "Notifications parse edilirken hata oluştu", e)
                }
            }

            Log.d(TAG, """
                LLM mesajları alındı:
                - Messages count: ${messages.size}
                - Tasks count: ${tasks.size}
                - Notifications count: ${notifications.size}
            """.trimIndent())

            // 3. Create models for saving
            val dailyData = DailyData(
                depressionScore = localDailyData.phq9Score ?: 0,
                steps = Steps(
                    steps = localDailyData.steps ?: 0,
                    isLeavedHome = localDailyData.isLeavedHome ?: false,
                    burnedCalorie = localDailyData.burnedCalorie ?: 0
                ),
                sleep = Sleep(
                    duration = localDailyData.sleepDuration ?: 0.0,
                    quality = localDailyData.sleepQuality ?: "unknown",
                    sleepStartTime = localDailyData.sleepStartTime ?: "00:00",
                    sleepEndTime = localDailyData.sleepEndTime ?: "00:00"
                ),
                mood = localDailyData.mood ?: 0,
                screenTime = ScreenTime(
                    total = localDailyData.screenTimeTotal ?: 0.0,
                    byApp = localDailyData.screenTimeByApp ?: emptyMap()
                )
            )

            val dailyLLM = DailyLLM(
                messages = messages,
                tasks = tasks,
                notifications = notifications
            )

            val phq9Result = localDailyData.phq9Score?.let { score ->
                PHQ9Result(
                    score = score,
                    answers = localDailyData.phq9Answers ?: emptyList(),
                    date = todayStr
                )
            }

            // 4. Save to Firebase
            saveDailyData(userId, todayStr, dailyData)
            saveDailyLLM(userId, todayStr, dailyLLM)
            phq9Result?.let { savePHQ9Result(userId, it) }

            Log.d(TAG, "Senkronizasyon başarıyla tamamlandı")

        } catch (e: Exception) {
            Log.e(TAG, "Senkronizasyon sırasında hata oluştu", e)
            throw e
        }
    }

    private suspend fun saveDailyData(userId: String, date: String, dailyData: DailyData) {
        saveDailyDataUseCase(userId, date, dailyData).fold(
            onSuccess = { Log.d(TAG, "DailyData başarıyla kaydedildi") },
            onFailure = { Log.e(TAG, "DailyData kaydedilirken hata: ${it.message}") }
        )
    }

    private suspend fun saveDailyLLM(userId: String, date: String, dailyLLM: DailyLLM) {
        saveDailyLLMUseCase(userId, date, dailyLLM).fold(
            onSuccess = { Log.d(TAG, "DailyLLM başarıyla kaydedildi") },
            onFailure = { Log.e(TAG, "DailyLLM kaydedilirken hata: ${it.message}") }
        )
    }

    private suspend fun savePHQ9Result(userId: String, phq9Result: PHQ9Result) {
        savePHQ9ResultUseCase(userId, phq9Result).fold(
            onSuccess = { Log.d(TAG, "PHQ9Result başarıyla kaydedildi") },
            onFailure = { Log.e(TAG, "PHQ9Result kaydedilirken hata: ${it.message}") }
        )
    }

    companion object {
        private const val TAG = "SyncDailyDataUseCase"
    }
} 