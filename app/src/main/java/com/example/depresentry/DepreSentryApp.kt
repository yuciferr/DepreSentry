package com.example.depresentry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.depresentry.data.local.dao.AppStateDao
import kotlinx.coroutines.launch

@HiltAndroidApp
class DepreSentryApp : Application() {
    
    @Inject
    lateinit var appStateDao: AppStateDao

    override fun onCreate() {
        super.onCreate()
        
        initializeApp()
    }

    private fun initializeApp() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            // AppState'i initialize et
            appStateDao.initializeAppState()
        }
    }
}