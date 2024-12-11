package com.example.depresentry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.depresentry.data.local.dao.AppStateDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import android.util.Log
import kotlinx.coroutines.withContext
import androidx.room.Room
import com.example.depresentry.data.local.DepreSentryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class DepreSentryApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var appStateDao: AppStateDao

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        
        // Kritik olmayan işlemleri arka plana al
        applicationScope.launch(Dispatchers.IO) {
            initializeWorkManager()
            initializeAppState()
        }
    }

    private fun initializeWorkManager() {
        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManager.initialize(
            this,
            config
        )
    }

    private suspend fun initializeAppState() {
        withContext(Dispatchers.IO) {
            appStateDao.initializeAppState()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}