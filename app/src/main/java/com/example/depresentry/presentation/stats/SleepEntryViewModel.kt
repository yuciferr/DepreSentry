package com.example.depresentry.presentation.stats


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.userData.local.UpdateSleepUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SleepEntryViewModel @Inject constructor(
    private val updateSleepUseCase: UpdateSleepUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepEntryUiState())
    val uiState: StateFlow<SleepEntryUiState> = _uiState.asStateFlow()

    fun onStartTimeChanged(time: LocalTime) {
        _uiState.update { currentState ->
            currentState.copy(
                startTime = time,
                duration = calculateDuration(time, currentState.endTime)
            )
        }
    }

    fun onEndTimeChanged(time: LocalTime) {
        _uiState.update { currentState ->
            currentState.copy(
                endTime = time,
                duration = calculateDuration(currentState.startTime, time)
            )
        }
    }

    fun onQualityChanged(quality: String) {
        _uiState.update { currentState ->
            currentState.copy(quality = quality)
        }
    }

    private fun calculateDuration(start: LocalTime, end: LocalTime): Double {
        val duration = if (end.isBefore(start)) {
            val endPlusDay = end.plusHours(24)
            val minutes = Duration.between(start, endPlusDay).toMinutes()
            minutes.toDouble() / 60.0
        } else {
            val minutes = Duration.between(start, end).toMinutes()
            minutes.toDouble() / 60.0
        }
        return duration
    }

    fun saveSleepData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val state = uiState.value
                
                updateSleepUseCase(
                    userId = userId,
                    duration = state.duration,
                    quality = state.quality,
                    startTime = state.startTime.toString(),
                    endTime = state.endTime.toString()
                )
                
                Log.d("SleepEntryViewModel", "Sleep data saved successfully: Duration=${state.duration}, Quality=${state.quality}")
            } catch (e: Exception) {
                Log.e("SleepEntryViewModel", "Error saving sleep data", e)
            }
        }
    }
}

data class SleepEntryUiState(
    val startTime: LocalTime = LocalTime.of(22, 0),
    val endTime: LocalTime = LocalTime.of(7, 0),
    val quality: String = "Good",
    val duration: Double = 9.0
) 