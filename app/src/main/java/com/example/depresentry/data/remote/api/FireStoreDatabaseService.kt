package com.example.depresentry.data.remote.api

import android.util.Log
import com.example.depresentry.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

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

    suspend fun saveDailyData(userId: String, date: String, dailyData: DailyData): Result<Boolean> {
        return try {
            val dataWithDate = dailyData.copy(date = date)
            
            firestore.collection("users")
                .document(userId)
                .collection("dailyData")
                .document(date)
                .set(dataWithDate)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyData(userId: String, date: String): Result<DailyData?> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .collection("dailyData")
                .document(date)
                .get()
                .await()
            
            Result.success(document.toObject(DailyData::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePHQ9Result(userId: String, phq9Result: PHQ9Result): Result<Boolean> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("phq9Results")
                .document(phq9Result.date)
                .set(phq9Result)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPHQ9Results(userId: String): Result<List<PHQ9Result>> {
        return try {
            val documents = firestore.collection("users")
                .document(userId)
                .collection("phq9Results")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val results = documents.mapNotNull { it.toObject(PHQ9Result::class.java) }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveDailyLLM(userId: String, date: String, dailyLLM: DailyLLM): Result<Boolean> {
        return try {
            val llmWithDate = dailyLLM.copy(date = date)
            
            firestore.collection("users")
                .document(userId)
                .collection("dailyLLM")
                .document(date)
                .set(llmWithDate)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyLLM(userId: String, date: String): Result<DailyLLM?> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .collection("dailyLLM")
                .document(date)
                .get()
                .await()
            
            Result.success(document.toObject(DailyLLM::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklyData(userId: String, startDate: String, endDate: String): Result<List<DailyData>> {
        return try {
            val documents = firestore.collection("users")
                .document(userId)
                .collection("dailyData")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            val dailyDataList = documents.mapNotNull { it.toObject(DailyData::class.java) }
            Result.success(dailyDataList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMonthlyData(userId: String, yearMonth: String): Result<List<DailyData>> {
        return try {
            val ym = YearMonth.parse(yearMonth)
            val startDate = ym.atDay(1).format(DateTimeFormatter.ISO_DATE)
            val endDate = ym.atEndOfMonth().format(DateTimeFormatter.ISO_DATE)

            val documents = firestore.collection("users")
                .document(userId)
                .collection("dailyData")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            val dailyDataList = documents.mapNotNull { it.toObject(DailyData::class.java) }
            Result.success(dailyDataList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeeklyLLM(userId: String, startDate: String, endDate: String): Result<List<DailyLLM>> {
        return try {
            val documents = firestore.collection("users")
                .document(userId)
                .collection("dailyLLM")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            val dailyLLMList = documents.mapNotNull { it.toObject(DailyLLM::class.java) }
            Result.success(dailyLLMList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMonthlyLLM(userId: String, yearMonth: String): Result<List<DailyLLM>> {
        return try {
            val ym = YearMonth.parse(yearMonth)
            val startDate = ym.atDay(1).format(DateTimeFormatter.ISO_DATE)
            val endDate = ym.atEndOfMonth().format(DateTimeFormatter.ISO_DATE)

            val documents = firestore.collection("users")
                .document(userId)
                .collection("dailyLLM")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .await()

            val dailyLLMList = documents.mapNotNull { it.toObject(DailyLLM::class.java) }
            Result.success(dailyLLMList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}