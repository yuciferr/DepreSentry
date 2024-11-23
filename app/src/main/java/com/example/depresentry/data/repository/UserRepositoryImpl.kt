package com.example.depresentry.data.repository

import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import android.util.Log
import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.UserRepository
import com.example.depresentry.data.remote.api.FirebaseAuthService
import com.example.depresentry.data.local.entity.ProfileImageEntity
import com.example.depresentry.data.local.dao.ProfileImageDao

import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val databaseService: FireStoreDatabaseService,
    private val profileImageDao: ProfileImageDao
) : UserRepository {
    override fun getCurrentUserId(): String {
        return authService.getUserId()
    }

    override suspend fun registerUser(userCredential: UserCredentials): Result<Boolean> {
        return authService.registerUser(userCredential.email, userCredential.password)
            .map { it.isNotEmpty() } // UID varsa true olarak dön
    }

    override suspend fun loginUser(userCredential: UserCredentials): Result<Boolean> {
        return authService.loginUser(userCredential.email, userCredential.password)
    }

    override suspend fun deleteUserAccount(): Result<Boolean> {
        return authService.deleteUserAccount()
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return authService.resetPassword(email)
    }

    override suspend fun createUserProfile(userProfile: UserProfile): Result<Boolean> {
        Log.d("SignUp", "REPOSİTORY Profile created successfully.")
        return databaseService.createUserProfile(userProfile)
    }

    override suspend fun getUserProfile(userUid: String): Result<UserProfile?> {
        return databaseService.getUserProfile(userUid)
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Result<Boolean> {
        return databaseService.updateUserProfile(userProfile)
    }

    override suspend fun saveProfileImageLocally(userId: String, imagePath: String): Result<Boolean> {
        return try {
            profileImageDao.insertProfileImage(
                ProfileImageEntity(
                    userId = userId,
                    imagePath = imagePath
                )
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLocalProfileImage(userId: String): Result<String?> {
        return try {
            val profileImage = profileImageDao.getProfileImage(userId)
            Result.success(profileImage?.imagePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logoutUser(): Result<Boolean> {
        return authService.logoutUser()
    }
}
