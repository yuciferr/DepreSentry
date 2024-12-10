package com.example.depresentry.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.usecase.CalculateDepressionScoreUseCase
import com.example.depresentry.domain.usecase.gemini.ProcessDailyDataUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
import java.util.Calendar

@HiltWorker
class DailyDataWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val calculateDepressionScoreUseCase: CalculateDepressionScoreUseCase,
    private val processDailyDataUseCase: ProcessDailyDataUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("DailyDataWorker", "Worker başlatıldı - ${Calendar.getInstance().time}")
            
            // Sahte DailyData oluştur
            val mockDailyData = DailyData(
                depressionScore = 0,
                steps = Steps(8000, true, 350),
                sleep = Sleep(7.5, "Good", "23:00", "06:30"),
                mood = 4,
                screenTime = ScreenTime(
                    total = 6.5,
                    byApp = mapOf(
                        "WhatsApp" to 2.5,
                        "YouTube" to 1.5,
                        "Instagram" to 1.0
                    )
                )
            )

            // Kullanıcı ID'sini al
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                Log.e("DailyDataWorker", "Kullanıcı ID alınamadı")
                return@withContext Result.failure()
            }

            Log.d("DailyDataWorker", "Kullanıcı ID alındı: $userId")

            // Kullanıcı profilini al
            val userProfile = getUserProfileUseCase(userId).getOrNull()
            if (userProfile == null) {
                Log.e("DailyDataWorker", "Kullanıcı profili alınamadı")
                return@withContext Result.failure()
            }

            Log.d("DailyDataWorker", "Kullanıcı profili alındı")

            // Depresyon skorunu hesapla
            val depressionScore = calculateDepressionScoreUseCase(mockDailyData, userProfile)
            Log.d("DailyDataWorker", "Depresyon skoru hesaplandı: $depressionScore")

            // Güncellenmiş DailyData'yı oluştur
            val updatedDailyData = mockDailyData.copy(depressionScore = depressionScore.toInt())

            // DailyData'yı Gemini'a gönder
            val processResult = processDailyDataUseCase(updatedDailyData)

            return@withContext if (processResult.isSuccess) {
                Log.d("DailyDataWorker", "Veri başarıyla Gemini'ye gönderildi - ${Calendar.getInstance().time}")
                Result.success()
            } else {
                val error = processResult.exceptionOrNull()
                Log.e("DailyDataWorker", "Veri Gemini'ye gönderilemedi", error)
                Log.e("DailyDataWorker", "Hata detayı: ${error?.message}")
                Log.e("DailyDataWorker", "Gönderilmeye çalışılan veri: $updatedDailyData")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DailyDataWorker", "Worker'da hata oluştu - ${Calendar.getInstance().time}", e)
            Result.failure()
        }
    }
}