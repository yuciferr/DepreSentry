package com.example.depresentry.data.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.depresentry.receiver.NotificationReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManagerService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "NotificationManagerService"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Depresentry Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily notifications from Depresentry"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(title: String, message: String, triggerTime: String) {
        try {
            val time = LocalTime.parse(triggerTime, DateTimeFormatter.ofPattern("HH:mm"))
            val now = LocalTime.now()
            
            // Zamanı milisaniyeye çevir
            var triggerAtMillis = System.currentTimeMillis()
            if (time.isBefore(now)) {
                // Eğer zaman geçmişse, yarına planla
                triggerAtMillis += 24 * 60 * 60 * 1000
            }
            triggerAtMillis += time.toSecondOfDay() * 1000L - now.toSecondOfDay() * 1000L

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_TITLE, title)
                putExtra(EXTRA_NOTIFICATION_MESSAGE, message)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )

            Log.d(TAG, "Notification scheduled for $triggerTime: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notification", e)
        }
    }

    companion object {
        const val CHANNEL_ID = "depresentry_notifications"
        const val EXTRA_NOTIFICATION_TITLE = "notification_title"
        const val EXTRA_NOTIFICATION_MESSAGE = "notification_message"
    }
} 