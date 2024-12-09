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
}