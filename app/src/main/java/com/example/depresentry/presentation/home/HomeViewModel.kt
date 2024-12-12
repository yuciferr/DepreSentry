package com.example.depresentry.presentation.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.usecase.CalculateDepressionScoreUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.example.depresentry.domain.usecase.usageStats.HasUsageStatsPermissionUseCase
import com.example.depresentry.domain.usecase.usageStats.GetDailyStatsUseCase
import com.example.depresentry.domain.usecase.usageStats.FormatDurationUseCase
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
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val formatDurationUseCase: FormatDurationUseCase
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

    private val _depressionScore = mutableStateOf(0.0)
    val depressionScore = _depressionScore as State<Double>

    private val _hasUsageStatsPermission = mutableStateOf(false)
    val hasUsageStatsPermission = _hasUsageStatsPermission as State<Boolean>

    private val _screenTimeStats = mutableStateOf<Map<String, Long>>(emptyMap())
    val screenTimeStats = _screenTimeStats as State<Map<String, Long>>

    init {
        loadUserProfile()
        loadTodayMessages()
        checkPermissionAndLoadScreenTime()
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
                _screenTimeStats.value = getDailyStatsUseCase()
            } catch (e: Exception) {
                Log.e("screen time", "Error loading screen time stats homescreen", e)
            }
        }
    }

    private fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    getUserProfileUseCase(userId).onSuccess { profile ->
                        profile?.let {
                            _fullName.value = it.fullName ?: ""
                        }
                    }

                    getLocalProfileImageUseCase(userId).onSuccess { localImagePath ->
                        _localProfileImagePath.value = localImagePath
                    }
                } finally {
                    _isLoading.value = false
                }
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

    fun formatDuration(millis: Long): String {
        return formatDurationUseCase(millis)
    }
} 