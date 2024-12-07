package com.example.depresentry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.RootNavGraph
import com.example.depresentry.presentation.theme.DepreSentryTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            DepreSentryTheme {
                GradientBackground()
                val navController = rememberNavController()
                RootNavGraph(navController = navController, context = this)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    DepreSentryTheme {
        GradientBackground()
    }
}