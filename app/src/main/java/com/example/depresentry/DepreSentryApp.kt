package com.example.depresentry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.depresentry.data.local.dao.AppStateDao
import kotlinx.coroutines.launch
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import android.util.Log

@HiltAndroidApp
class DepreSentryApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var appStateDao: AppStateDao

    override fun onCreate() {
        super.onCreate()
        
        // WorkManager'ı initialize et
        WorkManager.initialize(
            this,
            workManagerConfiguration
        )
        
        initializeApp()
    }

    private fun initializeApp() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            appStateDao.initializeAppState()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}