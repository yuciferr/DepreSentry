package com.example.depresentry.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.depresentry.presentation.calendar.CalendarScreen
import com.example.depresentry.presentation.home.HomeScreen
import com.example.depresentry.presentation.phq9.PHQ9Screen
import com.example.depresentry.presentation.profile.ProfileEditScreen
import com.example.depresentry.presentation.profile.ProfileScreen

sealed class MainScreen(val route: String) {
    object Home : MainScreen("home")
    object Calendar : MainScreen("calendar")
    object Profile : MainScreen("profile")
    object SleepStats : MainScreen("sleep_stats")
    object ActivityStats : MainScreen("activity_stats")
    object ScreenTimeStats : MainScreen("screen_time_stats")
    object MoodStats : MainScreen("mood_stats")
    object MoodEntry : MainScreen("mood_entry")
    object PHQ9 : MainScreen("phq9")
    object EditProfile : MainScreen("edit_profile")
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(startDestination = MainScreen.Home.route, route = RootScreen.Main.route) {
        composable(MainScreen.Home.route) { HomeScreen(navController) }
        composable(MainScreen.Calendar.route) { CalendarScreen(navController) }
        composable(MainScreen.Profile.route) { ProfileScreen(navController) }
        //composable(MainScreen.SleepStats.route) { SleepStatsScreen(navController) }
        //composable(MainScreen.ActivityStats.route) { ActivityStatsScreen(navController) }
        //composable(MainScreen.ScreenTimeStats.route) { ScreenTimeStatsScreen(navController) }
        //composable(MainScreen.MoodStats.route) { MoodStatsScreen(navController) }
        //composable(MainScreen.MoodEntry.route) { MoodEntryScreen(navController) }
        composable(MainScreen.PHQ9.route) { PHQ9Screen(navController) }
        composable(MainScreen.EditProfile.route) { ProfileEditScreen(navController) }
    }
}