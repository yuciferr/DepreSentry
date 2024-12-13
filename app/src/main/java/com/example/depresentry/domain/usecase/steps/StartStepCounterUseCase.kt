package com.example.depresentry.domain.usecase.steps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.depresentry.data.service.StepCounterService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StartStepCounterUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensorManager: SensorManager
) {
    operator fun invoke(): Boolean {
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.w("StartStepCounterUseCase", "Step sensor not available on this device")
            return false
        }

        return if (checkPermission()) {
            startService()
            true
        } else {
            false
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun startService() {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
} 