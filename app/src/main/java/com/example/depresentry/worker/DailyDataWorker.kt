package com.example.depresentry.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.depresentry.domain.usecase.gemini.ProcessDailyDataUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.usecase.gemini.GenerateAffirmationMessageUseCase
import com.example.depresentry.domain.usecase.gemini.GenerateDailyTodosUseCase
import com.example.depresentry.domain.usecase.gemini.GenerateNotificationsUseCase
import com.example.depresentry.domain.usecase.gemini.GenerateWelcomeMessageUseCase
import com.example.depresentry.domain.usecase.gemini.ProcessUserProfileUseCase
import com.example.depresentry.domain.usecase.userData.GetDailyDataUseCase
import java.util.Calendar
import java.time.LocalDate
import com.example.depresentry.data.remote.api.GeminiAIService
import com.example.depresentry.data.remote.api.GeminiState
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.model.Sleep
import kotlinx.coroutines.delay
import com.example.depresentry.domain.usecase.notification.ScheduleNotificationsUseCase

@HiltWorker
class DailyDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getDailyDataUseCase: GetDailyDataUseCase,
    private val processUserProfileUseCase: ProcessUserProfileUseCase,
    private val processDailyDataUseCase: ProcessDailyDataUseCase,
    private val generateWelcomeMessageUseCase: GenerateWelcomeMessageUseCase,
    private val generateAffirmationMessageUseCase: GenerateAffirmationMessageUseCase,
    private val generateDailyTodosUseCase: GenerateDailyTodosUseCase,
    private val generateNotificationsUseCase: GenerateNotificationsUseCase,
    private val geminiAIService: GeminiAIService,
    private val scheduleNotificationsUseCase: ScheduleNotificationsUseCase
) : CoroutineWorker(context, params) {

    private suspend fun waitForGeminiProcess(timeout: Long = 30000L): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeout) {
            when (geminiAIService.state.value) {
                is GeminiState.Idle -> return true
                is GeminiState.Error -> return false
                is GeminiState.Success -> return true
                is GeminiState.Processing -> delay(100)
            }
        }
        return false
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("DailyDataWorker", "Worker başlatıldı - ${Calendar.getInstance().time}")
            
            // 1. Kullanıcı ID'sini al
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                Log.e("DailyDataWorker", "Kullanıcı ID alınamadı")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Kullanıcı ID alındı: $userId")

            // 2. Kullanıcı profilini al
            val userProfile = getUserProfileUseCase(userId).getOrNull()
            if (userProfile == null) {
                Log.e("DailyDataWorker", "Kullanıcı profili alınamadı")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Kullanıcı profili alındı")

            // 4. Profili Gemini'ye gönder
            val profileResult = processUserProfileUseCase(userProfile)
            if (profileResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Profil Gemini'ye gönderilemedi veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Profil Gemini'ye gönderildi")

            // 3. Bir önceki günün verisini al
            val yesterday = LocalDate.now().minusDays(1).toString()
            val dailyData = getDailyDataUseCase(userId, yesterday).getOrNull()
            
            // 5. Günlük veriyi Gemini'ye gönder (veri varsa gerçek veri, yoksa default veri)
            val dataToSend = dailyData ?: DailyData(
                depressionScore = 50,
                steps = Steps(
                    steps = 0,
                    isLeavedHome = false,
                    burnedCalorie = 0
                ),
                sleep = Sleep(
                    duration = 0.0,
                    quality = "low",
                    sleepStartTime = "00:00",
                    sleepEndTime = "00:00"
                ),
                mood = 3,
                screenTime = ScreenTime(
                    total = 0.0,
                    byApp = emptyMap()
                )
            )

            val dailyDataResult = processDailyDataUseCase(dataToSend)
            if (dailyDataResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Günlük veri Gemini'ye gönderilemedi veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Günlük veri Gemini'ye gönderildi (${if (dailyData != null) "gerçek veri" else "default veri"})")

            // 6. Welcome mesajı oluştur
            val welcomeResult = generateWelcomeMessageUseCase()
            if (welcomeResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Welcome mesajı oluşturulamadı veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Welcome mesajı oluşturuldu")

            // 7. Affirmation mesajı oluştur
            val affirmationResult = generateAffirmationMessageUseCase()
            if (affirmationResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Affirmation mesajı oluşturulamadı veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Affirmation mesajı oluşturuldu")

            // 8. Günlük görevleri oluştur
            val todosResult = generateDailyTodosUseCase()
            if (todosResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Günlük görevler oluşturulamadı veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Günlük görevler oluşturuldu")

            // 9. Bildirim mesajlarını oluştur
            val notificationsResult = generateNotificationsUseCase()
            if (notificationsResult.isFailure || !waitForGeminiProcess()) {
                Log.e("DailyDataWorker", "Bildirim mesajları oluşturulamadı veya timeout oluştu")
                return@withContext Result.failure()
            }
            Log.d("DailyDataWorker", "Bildirim mesajları oluşturuldu")

            // 10. Bildirimleri zamanla
            try {
                scheduleNotificationsUseCase(userId)
                Log.d("DailyDataWorker", "Günlük bildirimler başarıyla zamanlandı")
            } catch (e: Exception) {
                Log.e("DailyDataWorker", "Bildirimler zamanlanırken hata oluştu", e)
                // Bildirimlerin kurulamaması kritik bir hata değil, worker'ı başarısız yapmıyoruz
            }

            Log.d("DailyDataWorker", "Tüm işlemler başarıyla tamamlandı - ${Calendar.getInstance().time}")
            Result.success()
        } catch (e: Exception) {
            Log.e("DailyDataWorker", "Worker'da hata oluştu - ${Calendar.getInstance().time}", e)
            Result.failure()
        }
    }
}