package com.example.depresentry

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.RootNavGraph
import com.example.depresentry.presentation.theme.DepreSentryTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.depresentry.data.local.dao.AppStateDao
import kotlinx.coroutines.launch
import com.example.depresentry.domain.model.DailyData
import com.example.depresentry.domain.model.Steps
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.domain.model.ScreenTime
import com.example.depresentry.domain.usecase.gemini.ProcessDailyDataUseCase
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

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

        // AppState'i initialize et ve bekle
        lifecycleScope.launch {
            appStateDao.initializeAppState()
            // AppState'in yüklenmesini bekle
            appStateDao.getAppState().first()
            
            // Mock data işlemi
            delay(1500) // Hilt'in injection'ı tamamlaması için kısa bir gecikme
            
            try {
                val userId = getCurrentUserIdUseCase()
                if (userId != null) { // Sadece kullanıcı login olmuşsa mock data işle
                    val mockDailyData = DailyData(
                        depressionScore = 75,
                        steps = Steps(8000, false, 350),
                        sleep = Sleep(4.5, "Good", "23:00", "06:30"),
                        mood = 2,
                        screenTime = ScreenTime(
                            total = 6.5,
                            byApp = mapOf(
                                "WhatsApp" to 2.5,
                                "YouTube" to 1.5,
                                "Instagram" to 1.0
                            )
                        )
                    )

                    processDailyDataUseCase(mockDailyData)
                }
            } catch (e: Exception) {
                // Hata durumunda loglama yapılabilir
                Log.e("MainActivity", "Mock data işleme hatası: ${e.message}")
            } finally {
                delay(1000) // Minimum splash screen süresi
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
    }
}