package com.example.depresentry.data.remote.api

import android.util.Log
import com.example.depresentry.domain.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FireStoreDatabaseService(
    private val firestore: FirebaseFirestore
) {
    suspend fun createUserProfile(userProfile: UserProfile): Result<Boolean> {
        Log.d("SignUp", "Creating user profile: $userProfile")
        return try {
            val userId = userProfile.userId
            Log.d("SignUp", "Setting value for user ID: $userId")

            // Firestore'a profil verilerini yazma
            firestore.collection("users")
                .document(userId)
                .set(userProfile)
                .await()

            Log.d("SignUp", "Profile created successfully for user ID: $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("SignUp", "Failed to create profile: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val userProfile = documentSnapshot.toObject(UserProfile::class.java)
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(newUserProfile: UserProfile): Result<Boolean> {
        return try {
            val userId = newUserProfile.userId
            val userRef = firestore.collection("users").document(userId)

            // Mevcut profili kontrol et
            val documentSnapshot = userRef.get().await()
            if (!documentSnapshot.exists()) {
                return Result.failure(Exception("Mevcut profil bulunamadı"))
            }

            // Değişiklikleri belirle ve güncelle
            val updates = calculateProfileUpdates(newUserProfile)
            if (updates.isNotEmpty()) {
                userRef.update(updates).await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateProfileUpdates(newUserProfile: UserProfile): Map<String, Any?> {
        val updates = mutableMapOf<String, Any?>()

        // Null değerleri de güncellemelere dahil ediyoruz
        updates["fullName"] = newUserProfile.fullName
        updates["gender"] = newUserProfile.gender
        updates["age"] = newUserProfile.age
        updates["profession"] = newUserProfile.profession
        updates["maritalStatus"] = newUserProfile.maritalStatus
        updates["country"] = newUserProfile.country
        updates["profileImage"] = newUserProfile.profileImage

        return updates
    }
}