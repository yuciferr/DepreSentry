package com.example.depresentry.presentation.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.Calendar
import com.example.depresentry.presentation.composables.GradientBackground
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(navController: NavHostController) {
    GradientBackground()
    Column(modifier = Modifier.fillMaxSize()) {
        val sampleMoodData = mapOf(
            LocalDate.of(2024, 10, 5) to 20, // Score of 20
            LocalDate.of(2024, 10, 6) to 47, // Score of 47
            LocalDate.of(2024, 10, 7) to 35, // Score of 35
            LocalDate.of(2024, 10, 10) to 80, // Score of 80
            LocalDate.of(2024, 10, 11) to 65, // Score of 65
            LocalDate.of(2024, 10, 12) to 53, // Score of 53
            LocalDate.of(2024, 10, 14) to 92, // Score of 92
            LocalDate.of(2024, 10, 15) to 78, // Score of 78
            LocalDate.of(2024, 10, 17) to 34, // Score of 34
            LocalDate.of(2024, 10, 20) to 49, // Score of 49
            LocalDate.of(2024, 10, 21) to 68, // Score of 68
            LocalDate.of(2024, 10, 22) to 33, // Score of 15
            LocalDate.of(2024, 10, 23) to 88, // Score of 88
            LocalDate.of(2024, 10, 24) to 77, // Score of 77
            LocalDate.of(2024, 10, 25) to 55, // Score of 55
            LocalDate.of(2024, 10, 26) to 23, // Score of 23
            LocalDate.of(2024, 10, 27) to 94, // Score of 94
            LocalDate.of(2024, 10, 28) to 62, // Score of 62
            LocalDate.of(2024, 10, 29) to 100, // Score of 39
            LocalDate.of(2024, 10, 30) to 1  // Score of 58
        )

        Calendar(
            currentMonth = YearMonth.now(),
            initialSelectedDate = LocalDate.now(),  // selectedDate yerine initialSelectedDate
            moodData = sampleMoodData,
            onDateSelected = {}
        )
        Spacer(modifier= Modifier.weight(1f))
        BottomNavigationBar(navController = navController)
    }
}
