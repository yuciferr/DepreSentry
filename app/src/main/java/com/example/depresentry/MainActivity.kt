package com.example.depresentry

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.depresentry.data.local.dao.AppStateDao
import com.example.depresentry.data.service.NotificationManagerService
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.steps.StartStepCounterUseCase
import com.example.depresentry.domain.usecase.userData.GetDailyDataUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalMessageByDateAndTypeAndRoleUseCase
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.RootNavGraph
import com.example.depresentry.presentation.theme.DepreSentryTheme
import com.example.depresentry.worker.DailyDataWorker
import com.example.depresentry.worker.ResetStepCounterWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Inject
    lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @Inject
    lateinit var getDailyDataUseCase: GetDailyDataUseCase

    @Inject
    lateinit var appStateDao: AppStateDao

    @Inject
    lateinit var getLocalMessageByDateAndTypeAndRoleUseCase: GetLocalMessageByDateAndTypeAndRoleUseCase

    @Inject
    lateinit var notificationManager: NotificationManagerService

    @Inject
    lateinit var startStepCounterUseCase: StartStepCounterUseCase

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplashScreen
            }
        }
        
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // WorkManager'ı arka planda başlat
        lifecycleScope.launch(Dispatchers.Default) {
            setupDailyDataWorker()
            //setupStepCounterResetWorker()
            withContext(Dispatchers.Main) {
                keepSplashScreen = false
            }
        }

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

        checkAndRequestNotificationPermission()
        checkAndRequestActivityRecognitionPermission()
    }

    private suspend fun setupDailyDataWorker() {
        try {
            // 1. Kullanıcı ID kontrolü
            val userId = getCurrentUserIdUseCase() ?: run {
                Log.e("WorkManager", "Kullanıcı ID bulunamadı")
                return
            }

            // 3. Bugünün notification_response mesajını kontrol et
            val today = LocalDate.now()
            val todayNotificationResponse = getLocalMessageByDateAndTypeAndRoleUseCase(
                userId = userId,
                date = today,
                messageType = "notifications_response",
                role = "model"
            )
            if (todayNotificationResponse != null){
                Log.e("WorkManager", "Bu gün için veri var. Worker kurulmadı.")
                return
            }

            // 2. Kullanıcı profili kontrolü
            val userProfile = getUserProfileUseCase(userId).getOrNull()
            if (userProfile == null) {
                Log.e("WorkManager", "Kullanıcı profili bulunamadı. Worker kurulmadı.")
                return
            }


            // 4. Bir önceki günün verilerini kontrol et
            val yesterday = LocalDate.now().minusDays(1).toString()
            val yesterdayData = getDailyDataUseCase(userId, yesterday).getOrNull()

            // Worker konfigürasyonu
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val currentDate = Calendar.getInstance()
            
            if (yesterdayData != null) {
                // Dün verisi varsa, sadece periyodik worker'ı kur (gece 00:30 için)
                val scheduledTime = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.SECOND, 0)
                }

                val initialDelay = scheduledTime.timeInMillis - currentDate.timeInMillis

                val dailyDataRequest = PeriodicWorkRequestBuilder<DailyDataWorker>(
                    24, TimeUnit.HOURS
                )
                    .setConstraints(constraints)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .addTag("daily_data_work")
                    .build()

                WorkManager.getInstance(applicationContext)
                    .enqueueUniquePeriodicWork(
                        "daily_data_work",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        dailyDataRequest
                    )

                Log.d("WorkManager", """
                    Periyodik Worker kuruldu
                    - Kullanıcı profili mevcut
                    - Dün verisi mevcut
                    - Planlanan çalışma zamanı: ${scheduledTime.time}
                """.trimIndent())

            } else {
                // Dün verisi yoksa, sadece immediate worker'ı kur
                val immediateWorkRequest = OneTimeWorkRequestBuilder<DailyDataWorker>()
                    .setConstraints(constraints)
                    .addTag("immediate_daily_data_work")
                    .build()

                WorkManager.getInstance(applicationContext)
                    .enqueue(immediateWorkRequest)

                Log.d("WorkManager", """
                    Immediate Worker kuruldu
                    - Kullanıcı profili mevcut
                    - Dün verisi yok
                    - Hemen çalışacak
                """.trimIndent())
            }

        } catch (e: Exception) {
            Log.e("WorkManager", "Worker kurulumunda hata: ${e.message}", e)
        }
    }

    private fun setupStepCounterResetWorker() {
        val constraints = Constraints.Builder()
            .build()

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        val resetWorkerRequest = PeriodicWorkRequestBuilder<ResetStepCounterWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "reset_step_counter",
                ExistingPeriodicWorkPolicy.UPDATE,
                resetWorkerRequest
            )
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
            // Kullanıcıya bildirim izni olmadan uygulamanın tam olarak çalışamayacağını bildir
        }
    }

    fun scheduleTestNotification(time: String) {
        try {
            notificationManager.scheduleNotification(
                title = "Test Bildirimi 🔔",
                message = "Bu bir test bildirimidir. Saat: $time",
                triggerTime = time
            )
            Log.d("MainActivity", "Test bildirimi zamanlandı: $time")
        } catch (e: Exception) {
            Log.e("MainActivity", "Test bildirimi zamanlanırken hata oluştu", e)
        }
    }

    private val activityRecognitionPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            startStepCounterUseCase()
        } else {
            // İzin verilmedi, kullanıcıya bilgi ver
            Log.d("MainActivity", "Some permissions were denied")
        }
    }

    private fun checkAndRequestActivityRecognitionPermission() {
        // Önce sensör var mı kontrol et
        val stepSensor = (getSystemService(Context.SENSOR_SERVICE) as SensorManager)
            .getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (stepSensor == null) {
            // Kullanıcıya bilgi ver
            Toast.makeText(
                this,
                "Step counter not available on this device",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                // ACTIVITY_RECOGNITION izni kontrolü
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED &&
                // HIGH_SAMPLING_RATE_SENSORS izni kontrolü (Android 12+)
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || 
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
                ) == PackageManager.PERMISSION_GRANTED) -> {
                    startStepCounterUseCase()
                }
                else -> {
                    // İzinleri iste
                    activityRecognitionPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
                            } else null
                        ).filterNotNull().toTypedArray()
                    )
                }
            }
        } else {
            startStepCounterUseCase()
        }
    }
}