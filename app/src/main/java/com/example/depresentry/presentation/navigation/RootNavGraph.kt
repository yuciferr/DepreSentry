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

@Composable
fun RootNavGraph(
    navController: NavHostController,
    context: Context
) {
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