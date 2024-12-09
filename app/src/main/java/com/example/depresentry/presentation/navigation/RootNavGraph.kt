package com.example.depresentry.presentation.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.depresentry.data.local.dao.AppStateDao
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RootNavGraph(
    navController: NavHostController,
    context: Context,
    appStateDao: AppStateDao
) {
    val appState by appStateDao.getAppState().collectAsState(initial = null)
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    // Debug log ekleyelim
    Log.d("RootNavGraph", "AppState: ${appState?.isOnboardingCompleted}")

    // Başlangıç destinasyonunu belirle
    val startDestination = when {
        // Aktif oturum varsa ana ekrana git
        currentUser != null -> RootScreen.Main.route
        // Diğer durumlar için auth flow'a git
        else -> RootScreen.Auth.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController, appState?.isOnboardingCompleted ?: false)
        mainGraph(navController)
    }
}

sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth_graph")
    object Main : RootScreen("main_graph")
}