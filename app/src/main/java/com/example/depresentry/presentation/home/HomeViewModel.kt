package com.example.depresentry.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.usecase.CalculateDepressionScoreUseCase
import com.example.depresentry.domain.usecase.gemini.GenerateAffirmationMessageUseCase
import com.example.depresentry.domain.usecase.gemini.GenerateWelcomeMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val generateWelcomeMessageUseCase: GenerateWelcomeMessageUseCase,
    private val generateAffirmationMessageUseCase: GenerateAffirmationMessageUseCase,
    private val calculateDepressionScoreUseCase: CalculateDepressionScoreUseCase
) : ViewModel() {

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

    init {
        loadUserProfile()
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
} 