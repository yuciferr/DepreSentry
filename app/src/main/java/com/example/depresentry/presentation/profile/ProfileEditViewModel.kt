package com.example.depresentry.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.profile.UpdateUserProfileUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    // State variables to track profile data, loading, error, and success
    var fullName = mutableStateOf("")
    var age = mutableStateOf("")
    var profession = mutableStateOf("")
    var gender = mutableStateOf("")
    var maritalStatus = mutableStateOf("")
    var country = mutableStateOf("")
    var profileImage = mutableStateOf("")

    var isLoading = mutableStateOf(false)
    var updateError = mutableStateOf<String?>(null)
    var updateSuccess = mutableStateOf(false)

    // Load user profile
    fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            viewModelScope.launch {
                isLoading.value = true
                updateError.value = null

                val result = getUserProfileUseCase(userId)
                result.onSuccess { profile ->
                    profile?.let {
                        fullName.value = it.fullName ?: ""
                        age.value = it.age.toString()
                        profession.value = it.profession ?: ""
                        gender.value = it.gender ?: ""
                        maritalStatus.value = it.maritalStatus ?: ""
                        country.value = it.country ?: ""
                        profileImage.value = it.profileImage ?: ""
                    }
                }.onFailure { exception ->
                    updateError.value = exception.message
                }

                isLoading.value = false
            }
        } else {
            updateError.value = "Kullanıcı oturumu açmamış."
        }
    }

    // Update user profile
    fun updateUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            val updatedProfile = UserProfile(
                userId = userId,
                fullName = fullName.value,
                age = age.value.toIntOrNull(),
                profession = profession.value,
                gender = gender.value,
                maritalStatus = maritalStatus.value,
                country = country.value,
                profileImage = profileImage.value
            )

            viewModelScope.launch {
                isLoading.value = true
                updateError.value = null

                val result = updateUserProfileUseCase(updatedProfile)
                result.onSuccess {
                    updateSuccess.value = true
                }.onFailure { exception ->
                    updateError.value = exception.message
                }

                isLoading.value = false
            }
        } else {
            updateError.value = "Kullanıcı oturumu açmamış."
        }
    }
}
