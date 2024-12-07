package com.example.depresentry.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.auth.LogoutUserUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    var fullName = mutableStateOf("")
    var email = mutableStateOf("")
    var localProfileImagePath = mutableStateOf<String?>(null)
    var logoutSuccess = mutableStateOf(false)
    var isLoading = mutableStateOf(true)
        private set

    init {
        loadUserProfile()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUserUseCase().onSuccess {
                logoutSuccess.value = true
            }
        }
    }

    private fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            isLoading.value = true
            email.value = auth.currentUser?.email ?: ""
            
            viewModelScope.launch {
                try {
                    // Firestore'dan profil bilgilerini yükle
                    getUserProfileUseCase(userId).onSuccess { profile ->
                        profile?.let {
                            fullName.value = it.fullName ?: ""
                        }
                    }

                    // Room DB'den profil fotoğrafını yükle
                    getLocalProfileImageUseCase(userId).onSuccess { localImagePath ->
                        localProfileImagePath.value = localImagePath
                    }
                } finally {
                    isLoading.value = false
                }
            }
        }
    }
} 