package com.example.depresentry.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.data.local.dao.AppStateDao
import com.example.depresentry.data.local.entity.AppStateEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appStateDao: AppStateDao
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            appStateDao.updateAppState(AppStateEntity(isOnboardingCompleted = true))
        }
    }
}