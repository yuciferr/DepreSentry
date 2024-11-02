package com.example.depresentry.data.repository

import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import android.util.Log
import com.example.depresentry.domain.model.UserCredentials
import com.example.depresentry.domain.model.UserProfile
import com.example.depresentry.domain.repository.UserRepository
import com.example.depresentry.data.remote.api.FirebaseAuthService

import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val databaseService: FireStoreDatabaseService,
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
}
