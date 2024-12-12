package com.example.depresentry.presentation.stats

import com.example.depresentry.domain.usecase.usageStats.GetDailyStatsUseCase
import com.example.depresentry.domain.usecase.usageStats.GetWeeklyStatsUseCase
import com.example.depresentry.domain.usecase.usageStats.GetMonthlyStatsUseCase
import com.example.depresentry.domain.usecase.usageStats.HasUsageStatsPermissionUseCase
import com.example.depresentry.domain.usecase.usageStats.FormatDurationUseCase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class DetailedStatsViewModel @Inject constructor(
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val hasUsageStatsPermissionUseCase: HasUsageStatsPermissionUseCase,
    private val formatDurationUseCase: FormatDurationUseCase
) : ViewModel() {
    private val TAG = "DetailedStatsViewModel"

    private val _hasPermission = mutableStateOf(false)
    val hasPermission: State<Boolean> = _hasPermission

    private val _dailyStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val dailyStats: State<Map<String, Long>> = _dailyStats

    private val _weeklyStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val weeklyStats: State<Map<String, Long>> = _weeklyStats

    private val _monthlyStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val monthlyStats: State<Map<String, Long>> = _monthlyStats

    init {
        checkPermissionAndLoadStats()
    }

    private fun checkPermissionAndLoadStats() {
        _hasPermission.value = hasUsageStatsPermissionUseCase()
        if (_hasPermission.value) {
            loadAllStats()
        }
    }

    private fun loadAllStats() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading daily stats")
                _dailyStats.value = getDailyStatsUseCase()
                
                Log.d(TAG, "Loading weekly stats")
                _weeklyStats.value = getWeeklyStatsUseCase()
                
                Log.d(TAG, "Loading monthly stats")
                _monthlyStats.value = getMonthlyStatsUseCase()
                
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