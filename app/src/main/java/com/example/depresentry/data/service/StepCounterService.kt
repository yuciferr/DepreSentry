package com.example.depresentry.data.service

import android.app.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.depresentry.MainActivity
import com.example.depresentry.R
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.userData.local.UpdateActivityUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var updateActivityUseCase: UpdateActivityUseCase
    
    @Inject
    lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Inject
    lateinit var sensorManager: SensorManager

    private var stepSensor: Sensor? = null
    private var initialSteps = -1
    private var currentSteps = 0

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var updateJob: Job? = null

    private val _stepCount = MutableStateFlow(0)
    val stepCount = _stepCount.asStateFlow()

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "step_counter_channel"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_NAME = "Step Counter"
        private const val TAG = "StepCounterService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        
        initializeSensor()
        if (stepSensor == null) {
            Log.w(TAG, "Step sensor not available on this device")
            stopSelf()
            return
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startPeriodicUpdate()
    }

    private fun initializeSensor() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun startPeriodicUpdate() {
        updateJob = serviceScope.launch {
            while (isActive) {
                try {
                    getCurrentUserIdUseCase()?.let { userId ->
                        val burnedCalories = (currentSteps * 0.0376).toInt()
                        updateActivityUseCase(
                            userId = userId,
                            steps = currentSteps,
                            isLeavedHome = currentSteps > 1000,
                            burnedCalorie = burnedCalories
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating activity data", e)
                }
                delay(5 * 60 * 1000) // 5 dakikada bir güncelle
            }
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Step counter notification channel"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Step Counter Active")
            .setContentText("Steps today: $currentSteps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (initialSteps == -1) {
                    initialSteps = it.values[0].toInt()
                }
                
                currentSteps = it.values[0].toInt() - initialSteps
                
                serviceScope.launch {
                    _stepCount.emit(currentSteps)
                }

                // Bildirimi güncelle
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, createNotification())
                
                Log.d(TAG, "Steps: $currentSteps")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        serviceScope.cancel()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
} 