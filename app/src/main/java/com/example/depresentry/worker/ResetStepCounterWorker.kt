package com.example.depresentry.worker

import com.example.depresentry.domain.usecase.userData.local.UpdateActivityUseCase
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.depresentry.data.service.StepCounterService
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ResetStepCounterWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserIdUseCase() ?: return@withContext Result.failure()
            
            // Adım sayısını sıfırla
            updateActivityUseCase(
                userId = userId,
                steps = 0,
                isLeavedHome = false,
                burnedCalorie = 0
            )

            // StepCounterService'i yeniden başlat
            val intent = Intent(applicationContext, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            } else {
                applicationContext.startService(intent)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ResetStepCounterWorker", "Error resetting step counter", e)
            Result.failure()
        }
    }
} 