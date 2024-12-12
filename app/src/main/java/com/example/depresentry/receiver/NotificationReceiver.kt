package com.example.depresentry.receiver

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.depresentry.MainActivity
import com.example.depresentry.R
import com.example.depresentry.data.service.NotificationManagerService.Companion.CHANNEL_ID
import com.example.depresentry.data.service.NotificationManagerService.Companion.EXTRA_NOTIFICATION_MESSAGE
import com.example.depresentry.data.service.NotificationManagerService.Companion.EXTRA_NOTIFICATION_TITLE

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = "NotificationReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_NOTIFICATION_MESSAGE) ?: return

        // İzin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Notification permission not granted")
                return
            }
        }

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            Log.d(TAG, "Notification posted: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Error posting notification", e)
        }
    }
} 