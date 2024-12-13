package com.example.depresentry.presentation.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.usecase.CalculateDepressionScoreUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.example.depresentry.domain.usecase.usageStats.HasUsageStatsPermissionUseCase
import com.example.depresentry.domain.usecase.usageStats.GetDailyUsageStatsUseCase
import com.example.depresentry.domain.usecase.usageStats.FormatDurationUseCase
import com.example.depresentry.domain.usecase.userData.GetWeeklyDataUseCase
import com.example.depresentry.domain.usecase.userData.local.GetCurrentDailyDataUseCase
import com.example.depresentry.domain.usecase.usageStats.GetWeeklyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val calculateDepressionScoreUseCase: CalculateDepressionScoreUseCase,
    private val getLocalMessageByDateAndTypeAndRoleUseCase: GetLocalMessageByDateAndTypeAndRoleUseCase,
    private val hasUsageStatsPermissionUseCase: HasUsageStatsPermissionUseCase,
    private val getDailyUsageStatsUseCase: GetDailyUsageStatsUseCase,
    private val formatDurationUseCase: FormatDurationUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentDailyDataUseCase: GetCurrentDailyDataUseCase,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase
) : ViewModel() {

    companion object {
        private const val DEFAULT_WELCOME_MESSAGE = "Welcome! I hope you are having a wonderful day."
        private const val DEFAULT_AFFIRMATION_MESSAGE = "Every new day is an opportunity for new beginnings. Don't forget to believe in yourself!"
    }

    private val _fullName = mutableStateOf("")
    val fullName = _fullName as State<String>

    private val _localProfileImagePath = mutableStateOf<String?>(null)
    val localProfileImagePath = _localProfileImagePath as State<String?>

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading as State<Boolean>

    private val _welcomeMessage = mutableStateOf("")
    val welcomeMessage = _welcomeMessage as State<String>

    private val _affirmationMessage = mutableStateOf("")
    val affirmationMessage = _affirmationMessage as State<String>

    private val _calculatedDepressionScore = mutableStateOf<Double?>(null)
    val calculatedDepressionScore: State<Double?> = _calculatedDepressionScore

    private val _hasUsageStatsPermission = mutableStateOf(false)
    val hasUsageStatsPermission = _hasUsageStatsPermission as State<Boolean>

    private val _screenTimeStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val screenTimeStats = _screenTimeStats as State<Map<String, Long>>

    private val _weeklyMoodData = mutableStateOf<List<Int>>(emptyList())
    val weeklyMoodData: State<List<Int>> = _weeklyMoodData

    private val _currentDailySteps = mutableStateOf(0)
    val currentDailySteps: State<Int> = _currentDailySteps

    private val _currentDailySleepDuration = mutableStateOf(0.0)
    val currentDailySleepDuration: State<Double> = _currentDailySleepDuration

    private val _currentDailyMood = mutableStateOf<Int?>(null)
    val currentDailyMood: State<Int?> = _currentDailyMood

    private val _currentMoodText = mutableStateOf<String>("Unknown")
    val currentMoodText: State<String> = _currentMoodText

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true // Yükleme başladı
                
                val userId = getCurrentUserIdUseCase() ?: return@launch

                // Tüm veri yükleme işlemleri
                getUserProfileUseCase(userId).onSuccess { profile ->
                    profile?.let {
                        _fullName.value = it.fullName ?: ""
                    }
                }

                getLocalProfileImageUseCase(userId).onSuccess { localImagePath ->
                    _localProfileImagePath.value = localImagePath
                }

                loadTodayMessages()
                checkPermissionAndLoadScreenTime()
                loadWeeklyMoodData()
                loadCurrentDailyData()
                loadWeeklyScreenTimeStats()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data", e)
            } finally {
                _isLoading.value = false // Yükleme bitti (başarılı veya başarısız)
            }
        }
    }

    private fun checkPermissionAndLoadScreenTime() {
        _hasUsageStatsPermission.value = hasUsageStatsPermissionUseCase()
        if (_hasUsageStatsPermission.value) {
            loadScreenTimeStats()
        }
    }

    private fun loadScreenTimeStats() {
        viewModelScope.launch {
            try {
                _screenTimeStats.value = getDailyUsageStatsUseCase()
            } catch (e: Exception) {
                Log.e("screen time", "Error loading screen time stats homescreen", e)
            }
        }
    }

    private fun loadTodayMessages() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val today = LocalDate.now()
                    
                    // Welcome mesajı
                    val welcomeMessage = getLocalMessageByDateAndTypeAndRoleUseCase(
                        userId = userId,
                        date = today,
                        messageType = "welcome_response",
                        role = "model"
                    )
                    _welcomeMessage.value = welcomeMessage?.content ?: DEFAULT_WELCOME_MESSAGE

                    // Affirmation mesajı
                    val affirmationMessage = getLocalMessageByDateAndTypeAndRoleUseCase(
                        userId = userId,
                        date = today,
                        messageType = "affirmation_response",
                        role = "model"
                    )
                    _affirmationMessage.value = affirmationMessage?.content ?: DEFAULT_AFFIRMATION_MESSAGE

                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Mesajlar yüklenirken hata oluştu: ${e.message}")
                    // Hata durumunda da default mesajları göster
                    _welcomeMessage.value = DEFAULT_WELCOME_MESSAGE
                    _affirmationMessage.value = DEFAULT_AFFIRMATION_MESSAGE
                }
            }
        }
    }

    private fun loadWeeklyMoodData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val today = LocalDate.now()
                val weekStart = today.minusDays(6)
                
                getWeeklyDataUseCase(userId, weekStart.toString(), today.toString())
                    .onSuccess { weeklyData ->
                        _weeklyMoodData.value = weeklyData.map { it.mood }
                        Log.d("HomeViewModel", "Weekly mood data loaded: ${weeklyData.size} days")
                    }
                    .onFailure {
                        Log.e("HomeViewModel", "Error loading weekly mood data", it)
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error in loadWeeklyMoodData", e)
            }
        }
    }

    private fun loadCurrentDailyData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                
                getCurrentDailyDataUseCase(userId)?.let { dailyDataEntity ->
                    // Entity'yi domain modeline dönüştür
                    val dailyData = dailyDataEntity.toDailyData()

                    // Mevcut state güncellemeleri
                    _currentDailySteps.value = dailyData.steps.steps
                    _currentDailySleepDuration.value = dailyData.sleep.duration
                    _currentDailyMood.value = dailyData.mood
                    
                    // Mood text'i güncelle
                    _currentMoodText.value = dailyData.mood.toMoodText()

                    // Depression score hesaplama
                    getUserProfileUseCase(userId).onSuccess { userProfile ->
                        userProfile?.let { profile ->
                            val score = calculateDepressionScoreUseCase(dailyData, profile)
                            _calculatedDepressionScore.value = score
                            Log.d("HomeViewModel", "Depression score calculated: $score")
                        }
                    }

                    Log.d("HomeViewModel", "Daily data loaded and processed successfully")
                } ?: run {
                    Log.w("HomeViewModel", "No current daily data available")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading and processing daily data", e)
            }
        }
    }

    fun formatDuration(millis: Long): String {
        return formatDurationUseCase(millis)
    }

    // Entity'den domain modeline dönüşüm için extension function
    private fun DailyDataEntity.toDailyData(): DailyData {
        return DailyData(
            date = this.date.toString(),
            depressionScore = this.depressionScore ?: 0,
            steps = Steps(
                steps = this.steps ?: 0,
                isLeavedHome = this.isLeavedHome ?: false,
                burnedCalorie = this.burnedCalorie ?: 0
            ),
            sleep = Sleep(
                duration = this.sleepDuration ?: 0.0,
                quality = this.sleepQuality ?: "",
                sleepStartTime = this.sleepStartTime ?: "",
                sleepEndTime = this.sleepEndTime ?: ""
            ),
            mood = this.mood ?: 0,
            screenTime = ScreenTime(
                total = this.screenTimeTotal ?: 0.0,
                byApp = this.screenTimeByApp ?: emptyMap()
            )
        )
    }

    // Sınıf içinde private bir extension function olarak ekleyelim
    private fun Int?.toMoodText(): String {
        return when (this) {
            5 -> "Excellent"
            4 -> "Good"
            3 -> "Neutral"
            2 -> "Poor"
            1 -> "Terrible"
            else -> "Unknown"
        }
    }

    // Yeni eklenecek weekly screen time state ve fonksiyonu
    private val _weeklyScreenTimeStats = mutableStateOf<List<Int>>(emptyList())
    val weeklyScreenTimeStats: State<List<Int>> = _weeklyScreenTimeStats

    private fun loadWeeklyScreenTimeStats() {
        viewModelScope.launch {
            try {
                if (_hasUsageStatsPermission.value) {
                    val weeklyStats = getWeeklyStatsUseCase()
                    _weeklyScreenTimeStats.value = weeklyStats.values
                        .map { duration -> 
                            (duration / (1000 * 60)).toInt() // Milisaniyeyi dakikaya çevir
                        }
                        .takeLast(7) // Son 7 günün verisini al
                    
                    Log.d("HomeViewModel", "Weekly screen time stats loaded: ${_weeklyScreenTimeStats.value}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading weekly screen time stats", e)
                _weeklyScreenTimeStats.value = List(7) { 0 } // Hata durumunda boş liste
            }
        }
    }
} 