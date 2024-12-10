package com.example.depresentry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.depresentry.data.local.dao.AppStateDao
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.gemini.ProcessDailyDataUseCase
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.RootNavGraph
import com.example.depresentry.presentation.theme.DepreSentryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.depresentry.worker.DailyDataWorker
import java.util.Calendar
import androidx.work.OneTimeWorkRequestBuilder
import android.util.Log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var processDailyDataUseCase: ProcessDailyDataUseCase
    
    @Inject
    lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Inject
    lateinit var appStateDao: AppStateDao

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplashScreen
            }
        }
        
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Splash screen'i kapat
        lifecycleScope.launch {
            keepSplashScreen = false
        }

        // WorkManager'ı başlat
        setupDailyDataWorker()

        setContent {
            DepreSentryTheme {
                GradientBackground()
                val navController = rememberNavController()
                RootNavGraph(
                    navController = navController, 
                    context = this,
                    appStateDao = appStateDao
                )
            }
        }
    }

    private fun setupDailyDataWorker() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Her gün saat 14:40'da çalışacak şekilde ayarla
            val currentDate = Calendar.getInstance()
            val scheduledTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 57)
                set(Calendar.SECOND, 0)

                if (before(currentDate)) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val initialDelay = scheduledTime.timeInMillis - currentDate.timeInMillis

            val dailyDataRequest = PeriodicWorkRequestBuilder<DailyDataWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("daily_data_work")
                .build()

            WorkManager.getInstance(applicationContext).apply {
                cancelAllWorkByTag("daily_data_work")
                enqueueUniquePeriodicWork(
                    "daily_data_work",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    dailyDataRequest
                )
            }

            // Hemen çalıştırmak için
            val immediateWorkRequest = OneTimeWorkRequestBuilder<DailyDataWorker>()
                .setConstraints(constraints)
                .addTag("immediate_daily_data_work")
                .build()

            WorkManager.getInstance(applicationContext)
                .enqueue(immediateWorkRequest)

            Log.d("WorkManager", "Worker başarıyla kuruldu")
        } catch (e: Exception) {
            Log.e("WorkManager", "Worker kurulumunda hata: ${e.message}", e)
        }
    }
}