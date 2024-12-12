package com.example.depresentry.data.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.app.usage.UsageStatsManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsService @Inject constructor(
    private val context: Context
) {
    private val TAG = "UsageStatsService"
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getUsageStatsSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    fun getDailyStats(): Map<String, Long> {
        Log.d(TAG, "Fetching daily usage stats")
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        return getStatsForTimeRange(startTime, endTime, "Daily")
    }

    fun getWeeklyStats(): Map<String, Long> {
        Log.d(TAG, "Fetching weekly usage stats")
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        // Haftanın başlangıcına git
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek))
        
        val startTime = calendar.timeInMillis

        return getStatsForTimeRange(startTime, endTime, "Weekly")
    }

    fun getMonthlyStats(): Map<String, Long> {
        Log.d(TAG, "Fetching monthly usage stats")
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        // Ayın başlangıcına git
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val startTime = calendar.timeInMillis

        return getStatsForTimeRange(startTime, endTime, "Monthly")
    }

    private fun getStatsForTimeRange(startTime: Long, endTime: Long, period: String): Map<String, Long> {
        Log.d(TAG, "$period Stats - Start: ${formatTime(startTime)}, End: ${formatTime(endTime)}")

        val statsMap = mutableMapOf<String, Long>()
        
        try {
            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                startTime,
                endTime
            )

            Log.d(TAG, "Raw usage stats size: ${usageStats.size}")

            // Önce verileri bir map'e toplayalım
            usageStats
                .filter { it.totalTimeInForeground > 0 }
                .forEach { stats ->
                    try {
                        val appName = try {
                            packageManager.getApplicationLabel(
                                packageManager.getApplicationInfo(stats.packageName, 0)
                            ).toString()
                        } catch (e: PackageManager.NameNotFoundException) {
                            stats.packageName
                        }
                        
                        // Aynı uygulamanın sürelerini topla
                        statsMap[appName] = (statsMap[appName] ?: 0L) + stats.totalTimeInForeground
                        
                        Log.d(TAG, "$period Stats - App: $appName, Time: ${formatDuration(stats.totalTimeInForeground)}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing stats for package: ${stats.packageName}", e)
                    }
                }

            // Süreye göre sıralayalım
            val sortedStats = statsMap.toList()
                .sortedByDescending { (_, duration) -> duration }
                .toMap()

            Log.d(TAG, "$period Stats Summary:")
            var totalDuration = 0L
            sortedStats.forEach { (app, duration) ->
                totalDuration += duration
                Log.d(TAG, "- $app: ${formatDuration(duration)}")
            }
            Log.d(TAG, "Total duration: ${formatDuration(totalDuration)}")
            Log.d(TAG, "Total apps tracked: ${sortedStats.size}")

            return sortedStats
        } catch (e: Exception) {
            Log.e(TAG, "Error getting usage stats", e)
            return emptyMap()
        }
    }

    fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 -> String.format("%.1fh", hours + (minutes / 60.0))
            minutes > 0 -> String.format("%dm", minutes)
            else -> "< 1m"
        }
    }

    private fun formatTime(timeMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        return calendar.time.toString()
    }
} 