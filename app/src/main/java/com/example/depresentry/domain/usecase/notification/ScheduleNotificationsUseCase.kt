package com.example.depresentry.domain.usecase.notification

import android.util.Log
import com.example.depresentry.data.service.NotificationManagerService
import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.time.LocalDate
import javax.inject.Inject

class ScheduleNotificationsUseCase @Inject constructor(
    private val getLocalMessageByDateAndTypeAndRoleUseCase: GetLocalMessageByDateAndTypeAndRoleUseCase,
    private val notificationManager: NotificationManagerService,
    private val gson: Gson
) {
    private val TAG = "ScheduleNotificationsUseCase"

    suspend operator fun invoke(userId: String) {
        try {
            val today = LocalDate.now()
            
            // Local'den notification mesajını al
            val notificationMessage = getLocalMessageByDateAndTypeAndRoleUseCase(
                userId = userId,
                date = today,
                messageType = "notifications_response",
                role = "model"
            )

            if (notificationMessage == null) {
                Log.d(TAG, "No notifications found for today")
                return
            }

            // JSON'ı parse et
            val notifications = try {
                val jsonObject = JsonParser.parseString(notificationMessage.content).asJsonObject
                val notificationsArray = jsonObject.getAsJsonArray("notifications")
                gson.fromJson(notificationsArray, Array<Notification>::class.java).toList()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing notifications JSON", e)
                return
            }

            // Her bir bildirimi zamanla
            notifications.forEach { notification ->
                notificationManager.scheduleNotification(
                    title = notification.title,
                    message = notification.body,
                    triggerTime = notification.pushingTime
                )
                Log.d(TAG, "Scheduled notification: ${notification.title} for ${notification.pushingTime}")
            }



        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notifications", e)
        }
    }
} 