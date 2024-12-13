package com.example.depresentry.presentation.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.depresentry.domain.usecase.userData.local.GetCurrentDailyDataUseCase
import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.model.ScreenTime

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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    // Local Data use case
    private val getCurrentDailyDataUseCase: GetCurrentDailyDataUseCase
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

    init {
        Log.d(TAG, "ViewModel initialized")
        checkUsagePermissionAndLoadStats()
    }

    fun loadDataForScreen(screenType: String) {
        Log.d(TAG, "Loading data for screen: $screenType")
        when (screenType) {
            "Steps" -> {
                Log.d(TAG, "Loading Steps data")
                loadStepsData()
            }
            "Sleep" -> {
                Log.d(TAG, "Loading Sleep data")
                loadSleepData()
            }
            "Screen Time" -> {
                Log.d(TAG, "Loading Screen Time data")
                loadAllScreenTimeStats()
            }
            "Mood" -> {
                Log.d(TAG, "Loading Mood data")
                loadMoodData()
            }
        }
    }

    private fun loadStepsData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to load steps data")
                val userId = getCurrentUserIdUseCase() ?: run {
                    Log.e(TAG, "Failed to get user ID for steps data")
                    return@launch
                }
                
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                val weekStart = today.minusDays(6)
                val weekEnd = today

                Log.d(TAG, "Loading daily steps for date: $today")
                getDailyDataUseCase(userId, today.toString()).onSuccess { remoteData ->
                    if (remoteData != null) {
                        _dailySteps.value = remoteData.steps
                        Log.d(TAG, "Daily steps loaded from remote: ${remoteData.steps}")
                    } else {
                        getCurrentDailyDataUseCase(userId)?.let { localData ->
                            _dailySteps.value = localData.toSteps()
                            Log.d(TAG, "Daily steps loaded from local: ${localData.steps}")
                        } ?: Log.w(TAG, "No steps data available in both remote and local")
                    }
                }

                Log.d(TAG, "Loading weekly steps from $weekStart to $weekEnd")
                getWeeklyDataUseCase(userId, weekStart.toString(), weekEnd.toString())
                    .onSuccess { weeklyData ->
                        _weeklySteps.value = weeklyData.map { it.steps }
                        Log.d(TAG, "Weekly steps loaded successfully: ${weeklyData.size} days")
                    }

                Log.d(TAG, "Loading monthly steps for: $currentYearMonth")
                getMonthlyDataUseCase(userId, currentYearMonth.toString())
                    .onSuccess { monthlyData ->
                        _monthlySteps.value = monthlyData.map { it.steps }
                        Log.d(TAG, "Monthly steps loaded successfully: ${monthlyData.size} days")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading steps data", e)
            }
        }
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to load sleep data")
                val userId = getCurrentUserIdUseCase() ?: run {
                    Log.e(TAG, "Failed to get user ID for sleep data")
                    return@launch
                }
                
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                val weekStart = today.minusDays(6)
                val weekEnd = today

                Log.d(TAG, "Loading daily sleep for date: $today")
                getDailyDataUseCase(userId, today.toString()).onSuccess { remoteData ->
                    if (remoteData != null) {
                        _dailySleep.value = remoteData.sleep
                        Log.d(TAG, "Daily sleep loaded from remote: ${remoteData.sleep}")
                    } else {
                        getCurrentDailyDataUseCase(userId)?.let { localData ->
                            _dailySleep.value = localData.toSleep()
                            Log.d(TAG, "Daily sleep loaded from local: ${localData.sleepDuration}")
                        } ?: Log.w(TAG, "No sleep data available in both remote and local")
                    }
                }

                Log.d(TAG, "Loading weekly sleep from $weekStart to $weekEnd")
                getWeeklyDataUseCase(userId, weekStart.toString(), weekEnd.toString())
                    .onSuccess { weeklyData ->
                        _weeklySleep.value = weeklyData.map { it.sleep }
                        Log.d(TAG, "Weekly sleep loaded successfully: ${weeklyData.size} days")
                        Log.d(TAG, "Weekly sleep data: ${weeklyData.map { it.sleep }}")
                    }

                Log.d(TAG, "Loading monthly sleep for: $currentYearMonth")
                getMonthlyDataUseCase(userId, currentYearMonth.toString())
                    .onSuccess { monthlyData ->
                        _monthlySleep.value = monthlyData.map { it.sleep }
                        Log.d(TAG, "Monthly sleep loaded successfully: ${monthlyData.size} days")
                        Log.d(TAG, "Monthly sleep data: ${monthlyData.map { it.sleep }}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading sleep data", e)
            }
        }
    }

    private fun loadMoodData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to load mood data")
                val userId = getCurrentUserIdUseCase() ?: run {
                    Log.e(TAG, "Failed to get user ID for mood data")
                    return@launch
                }
                
                val today = LocalDate.now()
                val currentYearMonth = YearMonth.now()
                val weekStart = today.minusDays(6)
                val weekEnd = today

                Log.d(TAG, "Loading daily mood for date: $today")
                getDailyDataUseCase(userId, today.toString()).onSuccess { remoteData ->
                    if (remoteData != null) {
                        _dailyMood.value = remoteData.mood
                        Log.d(TAG, "Daily mood loaded from remote: ${remoteData.mood}")
                    } else {
                        getCurrentDailyDataUseCase(userId)?.let { localData ->
                            _dailyMood.value = localData.getMood()
                            Log.d(TAG, "Daily mood loaded from local: ${localData.mood}")
                        } ?: Log.w(TAG, "No mood data available in both remote and local")
                    }
                }

                Log.d(TAG, "Loading weekly mood from $weekStart to $weekEnd")
                getWeeklyDataUseCase(userId, weekStart.toString(), weekEnd.toString())
                    .onSuccess { weeklyData ->
                        _weeklyMood.value = weeklyData.map { it.mood }
                        Log.d(TAG, "Weekly mood loaded successfully: ${weeklyData.size} days")
                    }

                Log.d(TAG, "Loading monthly mood for: $currentYearMonth")
                getMonthlyDataUseCase(userId, currentYearMonth.toString())
                    .onSuccess { monthlyData ->
                        _monthlyMood.value = monthlyData.map { it.mood }
                        Log.d(TAG, "Monthly mood loaded successfully: ${monthlyData.size} days")
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
                
                if (hasPermission) {
                    Log.d(TAG, "Permission granted, loading screen time stats")
                    loadAllScreenTimeStats()
                } else {
                    Log.d(TAG, "Skipping screen time stats load - no permission")
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
                Log.d(TAG, "Starting to load all screen time stats")
                
                Log.d(TAG, "Loading daily stats")
                _dailyUsageStats.value = getDailyUsageStatsUseCase()
                Log.d(TAG, "Daily stats loaded: ${_dailyUsageStats.value.size} apps")
                
                Log.d(TAG, "Loading weekly stats")
                _weeklyUsageStats.value = getWeeklyStatsUseCase()
                Log.d(TAG, "Weekly stats loaded: ${_weeklyUsageStats.value.size} apps")
                
                Log.d(TAG, "Loading monthly stats")
                _monthlyUsageStats.value = getMonthlyStatsUseCase()
                Log.d(TAG, "Monthly stats loaded: ${_monthlyUsageStats.value.size} apps")
                
                Log.d(TAG, "All screen time stats loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading screen time stats", e)
            }
        }
    }

    fun formatDuration(millis: Long): String {
        return formatDurationUseCase(millis)
    }

    // Entity'den domain modeline dönüştürme extension fonksiyonları
    private fun DailyDataEntity.toSteps(): Steps {
        return Steps(
            steps = this.steps ?: 0,
            isLeavedHome = this.isLeavedHome ?: false,
            burnedCalorie = this.burnedCalorie ?: 0
        )
    }

    private fun DailyDataEntity.toSleep(): Sleep {
        return Sleep(
            duration = this.sleepDuration ?: 0.0,
            quality = this.sleepQuality ?: "",
            sleepStartTime = this.sleepStartTime ?: "",
            sleepEndTime = this.sleepEndTime ?: ""
        )
    }

    private fun DailyDataEntity.getMood(): Int {
        return this.mood ?: 0
    }

    private fun DailyDataEntity.toScreenTime(): ScreenTime {
        return ScreenTime(
            total = this.screenTimeTotal ?: 0.0,
            byApp = this.screenTimeByApp ?: emptyMap()
        )
    }
} 