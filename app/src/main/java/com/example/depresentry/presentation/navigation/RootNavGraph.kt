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
//    var startDestination by remember { mutableStateOf<String?>(null) }
//
//    // SharedPreferences'dan ilk giriş kontrolü
//    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//    val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
//
//    LaunchedEffect(Unit) {
//        // Firebase auth durumunu kontrol et
//        val currentUser = FirebaseAuth.getInstance().currentUser
//
//        startDestination = when {
//            // Kullanıcı zaten giriş yapmışsa
//            currentUser != null -> {
//                RootScreen.Main.route
//            }
//            // İlk kez giriş yapılıyorsa
//            isFirstLaunch -> {
//                // İlk girişi kaydet
//                prefs.edit().putBoolean("is_first_launch", false).apply()
//                AuthScreen.Onboarding.route
//            }
//            // Daha önce giriş yapılmış ama şu an logout durumundaysa
//            else -> {
//                AuthScreen.Login.route
//            }
//        }
//    }
//
//    // Firebase Auth state listener
//    DisposableEffect(Unit) {
//        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
//            auth.currentUser?.let {
//                // Kullanıcı giriş yaptığında Main screen'e yönlendir
//                navController.navigate(RootScreen.Main.route) {
//                    popUpTo(navController.graph.startDestinationId) {
//                        inclusive = true
//                    }
//                }
//            }
//        }
//
//        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
//
//        onDispose {
//            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
//        }
//    }
//
//    startDestination?.let { destination ->
//        NavHost(
//            navController = navController,
//            startDestination = destination
//        ) {
//            authGraph(navController)
//            mainGraph(navController)
//        }
//    }

    NavHost(
        navController = navController,
        startDestination = RootScreen.Auth.route
    ) {
        authGraph(navController)
        mainGraph(navController)
    }
}

sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth_graph")
    object Main : RootScreen("main_graph")
}
