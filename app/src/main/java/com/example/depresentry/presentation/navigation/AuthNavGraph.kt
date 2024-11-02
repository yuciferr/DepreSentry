package com.example.depresentry.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.depresentry.presentation.auth.login.LoginScreen
import com.example.depresentry.presentation.auth.signin.SignUpScreen
import com.example.depresentry.presentation.onboarding.OnboardingScreen

sealed class AuthScreen(val route: String) {
    object Onboarding : AuthScreen("onboarding")
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = AuthScreen.Onboarding.route, route = RootScreen.Auth.route) {
        composable(AuthScreen.Onboarding.route) { OnboardingScreen(navController) }
        composable(AuthScreen.Login.route) { LoginScreen(navController)}
        composable(AuthScreen.SignUp.route) {
            SignUpScreen(navController)
        }
    }
}
