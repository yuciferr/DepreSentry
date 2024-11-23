package com.example.depresentry.presentation.auth.signin

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.CreateUserProfileUseCase
import com.example.depresentry.domain.usecase.auth.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val createUserProfileUseCase: CreateUserProfileUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    var isLoading = mutableStateOf(false)
    var signUpError = mutableStateOf<String?>(null)
    var signUpSuccess = mutableStateOf(false)


    fun registerAndCreateProfile(email: String, password: String, fullName: String) {
        val userCredentials = UserCredentials(email = email, password = password, fullName = fullName)

        viewModelScope.launch {
            isLoading.value = true
            signUpError.value = null

            // Kayıt işlemi
            val registerResult = registerUserUseCase(userCredentials)
            registerResult.onSuccess {
                val currentUserIdResult = getCurrentUserIdUseCase()
                val userProfile = UserProfile(userId = currentUserIdResult!!, fullName = fullName, gender = null, age = null, profession = null, maritalStatus = null, country = null, profileImage = "")
                createProfile(userProfile)
            }.onFailure { exception ->
                signUpError.value = exception.message
                isLoading.value = false // Hata durumunda yükleme durdurulmalı
                Log.e("SignUp", "Registration failed: ${exception.message}")
            }
        }
    }

    private suspend fun createProfile(userProfile: UserProfile) {
        val profileResult = createUserProfileUseCase(userProfile)
        profileResult.onSuccess {
            signUpSuccess.value = true // Kayıt başarılıysa burada güncellenmeli
            Log.d("SignUp", "Profile created successfully.")
        }.onFailure { exception ->
            signUpError.value = exception.message
            Log.e("SignUp", "Profile creation failed: ${exception.message}")
        }
        isLoading.value = false // Profil oluşturma işlemi tamamlandığında yükleme durdurulmalı
    }


}
