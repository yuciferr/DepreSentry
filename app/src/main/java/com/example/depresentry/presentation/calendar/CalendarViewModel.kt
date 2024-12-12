package com.example.depresentry.presentation.calendar

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.Task
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.userData.GetMonthlyDataUseCase
import com.example.depresentry.domain.usecase.userData.GetMonthlyLLMUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getMonthlyDataUseCase: GetMonthlyDataUseCase,
    private val getMonthlyLLMUseCase: GetMonthlyLLMUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getLocalMessageUseCase: GetLocalMessageByDateAndTypeAndRoleUseCase,
    private val gson: Gson
) : ViewModel() {

    private val _uiState = mutableStateOf(CalendarUiState())
    val uiState: State<CalendarUiState> = _uiState

    init {
        loadCurrentMonth()
        loadTodayLocalData()
        onDateSelected(LocalDate.now())
    }

    private fun loadTodayLocalData() {
        val userId = getCurrentUserIdUseCase() ?: return
        val today = LocalDate.now()

        viewModelScope.launch {
            try {
                
                // Local tasks'ları yükle
                getLocalMessageUseCase(userId, today, "todos_response", "model")?.let { message ->
                    try {
                        val jsonObject = JsonParser.parseString(message.content).asJsonObject
                        val tasksArray = jsonObject.getAsJsonArray("tasks")
                        val taskListType = object : TypeToken<List<Task>>() {}.type
                        val todayTasks = gson.fromJson<List<Task>>(tasksArray, taskListType)
                        

                        // UI state'i güncelle
                        _uiState.value = _uiState.value.copy(
                            todayTasks = todayTasks
                        )
                    } catch (e: Exception) {
                    }
                }

            } catch (e: Exception) {
            }
        }
    }

    fun onMonthChanged(yearMonth: YearMonth) {

        _uiState.value = _uiState.value.copy(
            currentYearMonth = yearMonth,
            selectedDate = null,
            selectedDayData = null,
            selectedDayTasks = null
        )
        loadMonthData(yearMonth)
    }

    fun onDateSelected(date: LocalDate) {

        
        val selectedDayData = _uiState.value.monthlyData.find { 
            LocalDate.parse(it.date) == date
        }

        
        val selectedDayTasks = _uiState.value.monthlyLLM.find {
            LocalDate.parse(it.date) == date
        }?.tasks

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedDayData = selectedDayData,
            selectedDayTasks = selectedDayTasks,
            error = if (selectedDayData == null) "Bu tarih için veri bulunamadı" else null
        )
    }

    private fun loadCurrentMonth() {
        val currentYearMonth = YearMonth.now()

        _uiState.value = _uiState.value.copy(currentYearMonth = currentYearMonth)
        loadMonthData(currentYearMonth)
    }

    private fun loadMonthData(yearMonth: YearMonth) {
        val userId = getCurrentUserIdUseCase()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // DailyData'ları yükle
                getMonthlyDataUseCase(userId, yearMonth.toString()).fold(
                    onSuccess = { monthlyData ->

                        monthlyData?.forEach { data ->
                        }
                        _uiState.value = _uiState.value.copy(
                            monthlyData = monthlyData ?: emptyList()
                        )
                    },
                    onFailure = { error ->
                    }
                )

                // DailyLLM'leri yükle

                getMonthlyLLMUseCase(userId, yearMonth.toString()).fold(
                    onSuccess = { monthlyLLM ->

                        monthlyLLM?.forEach { llm ->

                            llm.tasks.forEach { task ->
                            }
                        }
                        _uiState.value = _uiState.value.copy(
                            monthlyLLM = monthlyLLM ?: emptyList()
                        )
                    },
                    onFailure = { error ->

                    }
                )
            } catch (e: Exception) {

            } finally {

                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

data class CalendarUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentYearMonth: YearMonth = YearMonth.now(),
    val monthlyData: List<DailyData> = emptyList(),
    val monthlyLLM: List<DailyLLM> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedDayData: DailyData? = null,
    val selectedDayTasks: List<Task>? = null,
    val todayTasks: List<Task> = emptyList()
)