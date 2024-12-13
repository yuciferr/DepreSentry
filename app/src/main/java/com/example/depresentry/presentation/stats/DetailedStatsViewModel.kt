package com.example.depresentry.presentation.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.usecase.usageStats.*
import com.example.depresentry.domain.usecase.userData.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.Steps
import java.time.LocalDate
import java.time.YearMonth
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase

@HiltViewModel
class DetailedStatsViewModel @Inject constructor(
    // Screen Time Use Cases
    private val getDailyUsageStatsUseCase: GetDailyUsageStatsUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val hasUsageStatsPermissionUseCase: HasUsageStatsPermissionUseCase,
    private val formatDurationUseCase: FormatDurationUseCase,
    // Common Data Use Cases
    private val getDailyDataUseCase: GetDailyDataUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getMonthlyDataUseCase: GetMonthlyDataUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {
    private val TAG = "DetailedStatsViewModel"

    // Screen Time States
    private val _hasUsagePermission = mutableStateOf(false)
    val hasUsagePermission: State<Boolean> = _hasUsagePermission

    private val _dailyUsageStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val dailyUsageStats: State<Map<String, Long>> = _dailyUsageStats

    private val _weeklyUsageStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val weeklyUsageStats: State<Map<String, Long>> = _weeklyUsageStats

    private val _monthlyUsageStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val monthlyUsageStats: State<Map<String, Long>> = _monthlyUsageStats

    // Steps States
    private val _dailySteps = mutableStateOf<Steps?>(null)
    val dailySteps: State<Steps?> = _dailySteps

    private val _weeklySteps = mutableStateOf<List<Steps>>(emptyList())
    val weeklySteps: State<List<Steps>> = _weeklySteps

    private val _monthlySteps = mutableStateOf<List<Steps>>(emptyList())
    val monthlySteps: State<List<Steps>> = _monthlySteps

    // Sleep States
    private val _dailySleep = mutableStateOf<Sleep?>(null)
    val dailySleep: State<Sleep?> = _dailySleep

    private val _weeklySleep = mutableStateOf<List<Sleep>>(emptyList())
    val weeklySleep: State<List<Sleep>> = _weeklySleep

    private val _monthlySleep = mutableStateOf<List<Sleep>>(emptyList())
    val monthlySleep: State<List<Sleep>> = _monthlySleep

    // Mood States
    private val _dailyMood = mutableStateOf<Int?>(null)
    val dailyMood: State<Int?> = _dailyMood

    private val _weeklyMood = mutableStateOf<List<Int>>(emptyList())
    val weeklyMood: State<List<Int>> = _weeklyMood

    private val _monthlyMood = mutableStateOf<List<Int>>(emptyList())
    val monthlyMood: State<List<Int>> = _monthlyMood

    var currentScreen: String = ""

    init {
        checkUsagePermissionAndLoadStats()
        
        when (currentScreen) {
            "Steps" -> {
                loadStepsData()
            }
            "Sleep" -> {
                loadSleepData()
            }
            "Screen Time" -> {
                loadAllScreenTimeStats()
            }
            "Mood" -> {
                loadMoodData()
            }
        }
    }

    private fun loadStepsData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                
                // Haftalık tarih aralığını hesapla
                val weekStart = today.minusDays(6) // Son 7 günü al
                val weekEnd = today

                // Günlük veri
                getDailyDataUseCase(userId, today.toString()).onSuccess { data ->
                    data?.let { _dailySteps.value = it.steps }
                }

                // Haftalık veri
                getWeeklyDataUseCase(
                    userId,
                    weekStart.toString(),
                    weekEnd.toString()
                ).onSuccess { weeklyData ->
                    _weeklySteps.value = weeklyData.map { it.steps }
                }

                // Aylık veri
                getMonthlyDataUseCase(
                    userId,
                    currentYearMonth.toString()
                ).onSuccess { monthlyData ->
                    _monthlySteps.value = monthlyData.map { it.steps }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading steps data", e)
            }
        }
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                
                val weekStart = today.minusDays(6)
                val weekEnd = today

                // Günlük veri
                getDailyDataUseCase(userId, today.toString()).onSuccess { data ->
                    data?.let { _dailySleep.value = it.sleep }
                }

                // Haftalık veri
                getWeeklyDataUseCase(
                    userId,
                    weekStart.toString(),
                    weekEnd.toString()
                ).onSuccess { weeklyData ->
                    _weeklySleep.value = weeklyData.map { it.sleep }
                }

                // Aylık veri
                getMonthlyDataUseCase(
                    userId,
                    currentYearMonth.toString()
                ).onSuccess { monthlyData ->
                    _monthlySleep.value = monthlyData.map { it.sleep }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading sleep data", e)
            }
        }
    }

    private fun loadMoodData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                
                val weekStart = today.minusDays(6)
                val weekEnd = today

                // Günlük veri
                getDailyDataUseCase(userId, today.toString()).onSuccess { data ->
                    data?.let { _dailyMood.value = it.mood }
                }

                // Haftalık veri
                getWeeklyDataUseCase(
                    userId,
                    weekStart.toString(),
                    weekEnd.toString()
                ).onSuccess { weeklyData ->
                    _weeklyMood.value = weeklyData.map { it.mood }
                }

                // Aylık veri
                getMonthlyDataUseCase(
                    userId,
                    currentYearMonth.toString()
                ).onSuccess { monthlyData ->
                    _monthlyMood.value = monthlyData.map { it.mood }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading mood data", e)
            }
        }
    }

     fun checkUsagePermissionAndLoadStats() {
        viewModelScope.launch {
            try {
                val hasPermission = hasUsageStatsPermissionUseCase()
                Log.d(TAG, "Usage stats permission check: $hasPermission")
                _hasUsagePermission.value = hasPermission
                
                if (hasPermission && currentScreen == "Screen Time") {
                    loadAllScreenTimeStats()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking usage stats permission", e)
                _hasUsagePermission.value = false
            }
        }
    }

    private fun loadAllScreenTimeStats() {
        if (!_hasUsagePermission.value) {
            Log.d(TAG, "Skipping stats load - no permission")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading daily stats")
                _dailyUsageStats.value = getDailyUsageStatsUseCase()
                
                Log.d(TAG, "Loading weekly stats")
                _weeklyUsageStats.value = getWeeklyStatsUseCase()
                
                Log.d(TAG, "Loading monthly stats")
                _monthlyUsageStats.value = getMonthlyStatsUseCase()
                
                Log.d(TAG, "All stats loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading stats", e)
            }
        }
    }

    fun formatDuration(millis: Long): String {
        return formatDurationUseCase(millis)
    }
} 