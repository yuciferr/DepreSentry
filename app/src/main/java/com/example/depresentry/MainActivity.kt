package com.example.depresentry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.RootNavGraph
import com.example.depresentry.presentation.theme.DepreSentryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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