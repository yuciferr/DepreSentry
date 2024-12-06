package com.example.depresentry.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.DisposableEffect

@Composable
fun RootNavGraph(
    navController: NavHostController,
    context: Context
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Tek bir DisposableEffect kullanarak auth durumunu yönetiyoruz
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            startDestination = if (auth.currentUser != null) {
                RootScreen.Main.route
            } else {
                RootScreen.Auth.route
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        
        // İlk kontrol
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
            RootScreen.Main.route
        } else {
            RootScreen.Auth.route
        }

        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        }
    }

    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination
        ) {
            authGraph(navController)
            mainGraph(navController)
        }
    }
}

sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth_graph")
    object Main : RootScreen("main_graph")
}