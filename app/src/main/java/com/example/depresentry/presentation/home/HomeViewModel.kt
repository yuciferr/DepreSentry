package com.example.depresentry.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.*
import com.example.depresentry.domain.usecase.gemini.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val processUserProfileUseCase: ProcessUserProfileUseCase,
    private val processDailyDataUseCase: ProcessDailyDataUseCase,
    private val generateWelcomeMessageUseCase: GenerateWelcomeMessageUseCase,
    private val generateAffirmationMessageUseCase: GenerateAffirmationMessageUseCase,
    private val generateDailyTodosUseCase: GenerateDailyTodosUseCase,
    private val generateNotificationsUseCase: GenerateNotificationsUseCase
) : ViewModel() {

    private val TAG = "GeminiAI"

    init {
        processInitialData()
    }

    private fun processInitialData() {
        viewModelScope.launch {
            try {
                // Mock UserProfile
                val userProfile = UserProfile(
                    userId = "test_user_123",
                    fullName = "Yusuf Samed Celik",
                    age = 24,
                    profession = "Student",
                    gender = "Male",
                    maritalStatus = "Single",
                    country = "Turkey"
                )

                // Mock DailyData
                val dailyData = DailyData(
                    depressionScore = 70,
                    steps = Steps(
                        steps = 8700,
                        isLeavedHome = false,
                        burnedCalorie = 630
                    ),
                    sleep = Sleep(
                        duration = 7.5,
                        quality = "good",
                        sleepStartTime = "22:00",
                        sleepEndTime = "05:48"
                    ),
                    mood = 4,
                    screenTime = ScreenTime(
                        total = 5.2,
                        byApp = mapOf(
                            "Instagram" to 2.1,
                            "YouTube" to 1.5
                        )
                    )
                )

                // 1. İlk olarak UserProfile'ı işle
                processUserProfileUseCase(userProfile).getOrThrow()
                Log.d(TAG, "UserProfile başarıyla işlendi")

                // 2. Sonra DailyData'yı işle
                processDailyDataUseCase(dailyData).getOrThrow()
                Log.d(TAG, "DailyData başarıyla işlendi")

                // 3. Welcome Message'ı al
                val welcomeMessage = generateWelcomeMessageUseCase().getOrThrow()
                Log.d(TAG, "Karşılama mesajı: $welcomeMessage")

                // 4. Affirmation Message'ı al
                val affirmationMessage = generateAffirmationMessageUseCase().getOrThrow()
                Log.d(TAG, "Olumlu mesaj: $affirmationMessage")

                // 5. Daily Todos'ları al
                val todos = generateDailyTodosUseCase().getOrThrow()
                Log.d(TAG, "Günlük görevler: $todos")

                // 6. Notifications'ları al
                val notifications = generateNotificationsUseCase().getOrThrow()
                Log.d(TAG, "Bildirimler: $notifications")

            } catch (e: Exception) {
                Log.e(TAG, "Veri işleme hatası: ${e.message}", e)
                // Hata durumunda UI'ı bilgilendirmek için state güncellemesi yapılabilir
            }
        }
    }
} 