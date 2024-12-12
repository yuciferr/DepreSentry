package com.example.depresentry.presentation.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.DailyLLM
import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.model.PHQ9Result
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.model.Task
import com.example.depresentry.domain.usecase.CalculateDepressionScoreUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.auth.LogoutUserUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.userData.SyncDailyDataUseCase
import com.example.depresentry.domain.usecase.userData.SaveDailyDataUseCase
import com.example.depresentry.domain.usecase.userData.SaveDailyLLMUseCase
import com.example.depresentry.domain.usecase.userData.SavePHQ9ResultUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val syncDailyDataUseCase: SyncDailyDataUseCase,
    private val saveDailyDataUseCase: SaveDailyDataUseCase,
    private val saveDailyLLMUseCase: SaveDailyLLMUseCase,
    private val savePHQ9ResultUseCase: SavePHQ9ResultUseCase,
    private val calculateDepressionScoreUseCase: CalculateDepressionScoreUseCase,
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

    fun syncData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                Log.d("ProfileViewModel", "Senkronizasyon başlatılıyor...")
                syncDailyDataUseCase(userId)
                Log.d("ProfileViewModel", "Senkronizasyon başarıyla tamamlandı")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Senkronizasyon sırasında hata oluştu", e)
            }
        }
    }

    private suspend fun generateRandomDailyData(date: LocalDate): Triple<DailyData, DailyLLM, PHQ9Result?> {
        // Random ama tutarlı mood ve PHQ9 skorları
        val mood = (1..5).random()
        val phq9Score = when (mood) {
            1 -> (15..27).random() // Kötü ruh hali - yüksek depresyon skoru
            2 -> (10..15).random() // Kötüye yakın
            3 -> (5..10).random()  // Orta
            4 -> (1..5).random()   // İyi
            else -> 0              // Çok iyi
        }

        // PHQ9 cevapları - skor ile tutarlı
        val phq9Answers = List(9) { index ->
            when {
                phq9Score > 20 -> (2..3).random()  // Ağır depresyon
                phq9Score > 15 -> (1..3).random()  // Orta-ağır depresyon
                phq9Score > 10 -> (1..2).random()  // Orta depresyon
                phq9Score > 5 -> (0..2).random()   // Hafif depresyon
                else -> (0..1).random()            // Minimal depresyon
            }
        }

        // Uyku verileri için random değer üretme fonksiyonu
        fun randomDouble(from: Double, to: Double): Double {
            return from + (Math.random() * (to - from))
        }

        // Uyku süresi hesaplama
        val sleepDuration = when (mood) {
            1 -> randomDouble(3.0, 5.0)
            2 -> randomDouble(5.0, 6.0)
            3 -> randomDouble(6.0, 7.0)
            4 -> randomDouble(7.0, 8.0)
            else -> randomDouble(8.0, 9.0)
        }

        // Ekran süresi hesaplama
        val screenTime = when (mood) {
            1 -> randomDouble(6.0, 8.0)
            2 -> randomDouble(5.0, 7.0)
            3 -> randomDouble(4.0, 6.0)
            4 -> randomDouble(3.0, 5.0)
            else -> randomDouble(2.0, 4.0)
        }

        // Random task seçimi için task havuzu
        val taskPool = listOf(
            Task("Morning Walk", "Take a 10-minute walk outside", "pending"),
            Task("Mindfulness", "Practice deep breathing for 5 minutes", "pending"),
            Task("Self-Care", "Do something you enjoy today", "pending"),
            Task("Journaling", "Write down your thoughts and feelings", "pending"),
            Task("Social Connection", "Reach out to a friend or family member", "pending"),
            Task("Healthy Eating", "Have a balanced meal", "pending"),
            Task("Exercise", "Do some light stretching or yoga", "pending"),
            Task("Reading", "Read a few pages of a book", "pending"),
            Task("Music Therapy", "Listen to your favorite music", "pending"),
            Task("Nature Time", "Spend some time in nature", "pending"),
            Task("Gratitude", "Write down three things you're grateful for", "pending"),
            Task("Creative Activity", "Engage in a creative hobby", "pending"),
            Task("Digital Detox", "Take a short break from screens", "pending"),
            Task("Organization", "Organize a small space in your home", "pending"),
            Task("Relaxation", "Take a relaxing bath or shower", "pending")
        )

        // Günlük 3 random task seç
        val selectedTasks = taskPool.shuffled().take(3)

        // DailyLLM oluştur
        val dailyLLM = DailyLLM(
            messages = mapOf(
                "welcome" to "Good morning! Today might be challenging, but remember you're stronger than you think.",
                "affirmation" to "You're making progress every day, even if it doesn't feel like it.",
                "tasks" to "Focus on small, achievable goals today."
            ),
            tasks = selectedTasks,
            notifications = listOf(
                Notification(
                    title = "Morning Check-in",
                    body = "How are you feeling today?",
                    pushingTime = "09:00"
                ),
                Notification(
                    title = "Afternoon Reminder",
                    body = "Take a moment to breathe",
                    pushingTime = "14:00"
                ),
                Notification(
                    title = "Evening Reflection",
                    body = "Reflect on your day",
                    pushingTime = "20:00"
                )
            )
        )

        // Aktivite verileri - mood ile tutarlı
        val steps = when (mood) {
            1 -> (0..2000).random()
            2 -> (2000..4000).random()
            3 -> (4000..6000).random()
            4 -> (6000..8000).random()
            else -> (8000..10000).random()
        }

        // Uyku kalitesi seçenekleri
        val sleepQualities = listOf("poor", "below_average", "average", "good", "excellent")
        val sleepQuality = when (mood) {
            1 -> sleepQualities.take(2)  // poor, below_average
            2 -> sleepQualities.take(3).drop(1)  // below_average, average
            3 -> sleepQualities.take(4).drop(1)  // below_average, average, good
            4 -> sleepQualities.takeLast(3)  // average, good, excellent
            else -> sleepQualities.takeLast(2)  // good, excellent
        }.random()

        // Önce DailyData'yı oluştur
        val dailyData = DailyData(
            depressionScore = 0,  // Geçici değer
            steps = Steps(
                steps = steps,
                isLeavedHome = steps > 4000,
                burnedCalorie = (steps * 0.04).toInt()
            ),
            sleep = Sleep(
                duration = sleepDuration,
                quality = sleepQuality,
                sleepStartTime = "23:00",
                sleepEndTime = String.format("%02d:00", (sleepDuration + 23).toInt() % 24)
            ),
            mood = mood,
            screenTime = ScreenTime(
                total = screenTime,
                byApp = mapOf(
                    "Social Media" to (screenTime * 0.4),
                    "Productivity" to (screenTime * 0.3),
                    "Entertainment" to (screenTime * 0.2),
                    "Other" to (screenTime * 0.1)
                )
            )
        )

        // Kullanıcı profilini al
        val userId = getCurrentUserIdUseCase() ?: return Triple(dailyData, dailyLLM, null)
        val userProfile = getUserProfileUseCase(userId).getOrNull() ?: return Triple(dailyData, dailyLLM, null)

        // Depression score'u hesapla
        val depressionScore = calculateDepressionScoreUseCase(dailyData, userProfile)

        // Yeni depression score ile güncellenmiş DailyData
        val updatedDailyData = dailyData.copy(depressionScore = depressionScore.toInt())

        // PHQ9Result oluştur
        val phq9Result = if (date.dayOfMonth % 7 == 0) { // Her 7 günde bir PHQ9 testi
            PHQ9Result(
                score = phq9Score,
                answers = phq9Answers,
                date = date.toString()
            )
        } else null

        return Triple(updatedDailyData, dailyLLM, phq9Result)
    }

    fun generateHistoricalData(daysBack: Int) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                val today = LocalDate.now()

                for (i in daysBack downTo 1) {
                    val date = today.minusDays(i.toLong())
                    val (dailyData, dailyLLM, phq9Result) = generateRandomDailyData(date)
                    val dateStr = date.toString()

                    // Verileri kaydet
                    saveDailyDataUseCase(userId, dateStr, dailyData)
                    saveDailyLLMUseCase(userId, dateStr, dailyLLM)
                    phq9Result?.let { savePHQ9ResultUseCase(userId, it) }

                    Log.d("HistoricalData", "Generated data for date: $dateStr")
                }

                Log.d("HistoricalData", "Completed generating $daysBack days of historical data")
            } catch (e: Exception) {
                Log.e("HistoricalData", "Error generating historical data", e)
            }
        }
    }
} 