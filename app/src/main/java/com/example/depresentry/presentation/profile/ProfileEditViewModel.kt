package com.example.depresentry.presentation.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.profile.UpdateUserProfileUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.SaveLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.gemini.ProcessUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import android.content.Intent
import android.content.Context

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val saveLocalProfileImageUseCase: SaveLocalProfileImageUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val processUserProfileUseCase: ProcessUserProfileUseCase
) : ViewModel() {

    // State variables to track profile data, loading, error, and success
    var fullName = mutableStateOf("")
    var age = mutableStateOf("")
    var profession = mutableStateOf("")
    var gender = mutableStateOf("")
    var maritalStatus = mutableStateOf("")
    var country = mutableStateOf("")
    var profileImage = mutableStateOf("")
    var selectedProfileImageUri = mutableStateOf<Uri?>(null)


    var isLoading = mutableStateOf(false)
    var updateError = mutableStateOf<String?>(null)
    var updateSuccess = mutableStateOf(false)


    // Method to update selected profile image
    fun updateProfileImage(uri: Uri?, context: Context) {
        Log.d("yuci", "Updating profile image with uri: $uri")
        
        uri?.let { imageUri ->
            viewModelScope.launch {
                try {
                    isLoading.value = true
                    val userId = getCurrentUserIdUseCase() ?: return@launch
                    
                    // Kalıcı URI izni al
                    context.contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    
                    saveLocalProfileImageUseCase(userId, imageUri.toString())
                        .onSuccess {
                            Log.d("yuci", "Image saved successfully")
                            profileImage.value = imageUri.toString()
                            selectedProfileImageUri.value = imageUri
                        }
                        .onFailure { exception ->
                            Log.e("yuci", "Failed to save image: ${exception.message}")
                            updateError.value = "Failed to save image: ${exception.message}"
                        }
                } catch (e: Exception) {
                    Log.e("yuci", "Error updating image: ${e.message}")
                    updateError.value = "Error updating image: ${e.message}"
                } finally {
                    isLoading.value = false
                }
            }
        }
    }

    // Load user profile
    fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            viewModelScope.launch {
                try {
                    isLoading.value = true
                    updateError.value = null

                    Log.d("yuci", "Loading user profile for userId: $userId")

                    // 1. Firestore'dan profil bilgilerini yükle (fotoğraf hariç)
                    getUserProfileUseCase(userId).onSuccess { profile ->
                        Log.d("yuci", "Firestore profile loaded: $profile")
                        profile?.let {
                            fullName.value = it.fullName ?: ""
                            age.value = it.age?.toString() ?: ""
                            profession.value = it.profession ?: ""
                            gender.value = it.gender ?: ""
                            maritalStatus.value = it.maritalStatus ?: ""
                            country.value = it.country ?: ""
                        }
                    }.onFailure { exception ->
                        Log.e("yuci", "Firestore error: ${exception.message}")
                        updateError.value = "Firestore error: ${exception.message}"
                    }

                    // 2. Room DB'den profil fotoğrafını yükle
                    getLocalProfileImageUseCase(userId).onSuccess { localImagePath ->
                        Log.d("yuci", "Local image path from Room DB: $localImagePath")
                        if (!localImagePath.isNullOrEmpty()) {
                            try {
                                val uri = Uri.parse(localImagePath)
                                Log.d("yuci", "Parsed URI from local image path: $uri")
                                selectedProfileImageUri.value = uri
                                profileImage.value = localImagePath
                                Log.d("yuci", "Successfully set profile image states")
                            } catch (e: Exception) {
                                Log.e("yuci", "Error parsing URI: ${e.message}")
                            }
                        } else {
                            Log.d("yuci", "No local image path found in Room DB")
                        }
                    }.onFailure { exception ->
                        Log.e("yuci", "Local DB error: ${exception.message}")
                    }

                } catch (e: Exception) {
                    Log.e("yuci", "Error loading profile: ${e.message}")
                    updateError.value = "Error loading profile: ${e.message}"
                } finally {
                    isLoading.value = false
                }
            }
        } else {
            Log.e("yuci", "User ID is null")
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
                profileImage = ""
            )

            viewModelScope.launch {
                try {
                    isLoading.value = true
                    updateError.value = null

                    // Önce profili güncelle
                    updateUserProfileUseCase(updatedProfile)
                        .onSuccess {
                            // Başarılı olursa Gemini'ye profil mesajını gönder
                            processUserProfileUseCase(updatedProfile)
                                .onSuccess {
                                    updateSuccess.value = true
                                }
                                .onFailure { exception ->
                                    updateError.value = "Failed to process profile with Gemini: ${exception.message}"
                                }
                        }
                        .onFailure { exception ->
                            updateError.value = "Failed to update profile: ${exception.message}"
                        }
                } catch (e: Exception) {
                    updateError.value = "Error: ${e.message}"
                } finally {
                    isLoading.value = false
                }
            }
        } else {
            updateError.value = "Kullanıcı oturumu açmamış."
        }
    }
}
