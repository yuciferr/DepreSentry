package com.example.depresentry.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.depresentry.presentation.calendar.CalendarScreen
import com.example.depresentry.presentation.home.HomeScreen
import com.example.depresentry.presentation.mood.MoodEntryScreen
import com.example.depresentry.presentation.phq9.PHQ9Screen
import com.example.depresentry.presentation.profile.ProfileEditScreen
import com.example.depresentry.presentation.profile.ProfileScreen
import com.example.depresentry.presentation.stats.DetailedStatsScreen

sealed class MainScreen(val route: String, var title: String? = null) {
    object Home : MainScreen("home")
    object Calendar : MainScreen("calendar")
    object Profile : MainScreen("profile")
    object MoodEntry : MainScreen("mood_entry")
    object PHQ9 : MainScreen("phq9")
    object EditProfile : MainScreen("edit_profile")
    object DetailedStats : MainScreen("detailed_stats")
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(startDestination = MainScreen.Home.route, route = RootScreen.Main.route) {
        composable(MainScreen.Home.route) { HomeScreen(navController) }
        composable(MainScreen.Calendar.route) { CalendarScreen(navController) }
        composable(MainScreen.Profile.route) { ProfileScreen(navController) }
        composable(MainScreen.MoodEntry.route) { MoodEntryScreen(navController){} }
        composable(MainScreen.PHQ9.route) { PHQ9Screen(navController) }
        composable(MainScreen.EditProfile.route) { ProfileEditScreen(navController) }
        composable(MainScreen.DetailedStats.route) { DetailedStatsScreen(navController, MainScreen.DetailedStats.title!!) }
    }
}
