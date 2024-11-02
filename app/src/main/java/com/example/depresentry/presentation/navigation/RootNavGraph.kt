package com.example.depresentry.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth_graph")
    object Main : RootScreen("main_graph")
}

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = RootScreen.Auth.route) {
        authGraph(navController)
        mainGraph(navController)
    }
}
