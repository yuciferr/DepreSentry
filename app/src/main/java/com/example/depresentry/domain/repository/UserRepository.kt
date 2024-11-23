package com.example.depresentry.domain.repository

import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.model.UserProfile

interface UserRepository {
    fun getCurrentUserId(): String
    suspend fun registerUser(userCredential: UserCredentials): Result<Boolean>
    suspend fun loginUser(userCredential: UserCredentials): Result<Boolean>
    suspend fun deleteUserAccount(): Result<Boolean>
    suspend fun resetPassword(email: String): Result<Boolean>
    suspend fun createUserProfile(userProfile: UserProfile): Result<Boolean>
    suspend fun getUserProfile(userId: String): Result<UserProfile?>
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Boolean>
    suspend fun saveProfileImageLocally(userId: String, imagePath: String): Result<Boolean>
    suspend fun getLocalProfileImage(userId: String): Result<String?>
    suspend fun logoutUser(): Result<Boolean>
}
