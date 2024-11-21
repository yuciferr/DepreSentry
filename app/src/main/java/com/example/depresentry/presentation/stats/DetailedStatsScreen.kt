package com.example.depresentry.presentation.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.common.model.Point
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.MoodLineChart
import com.example.depresentry.presentation.composables.MoodLineChartWithInteractivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailedStatsScreen(navController: NavController, title: String) {
    // Remember the selected mood (1 to 5) based on button clicks
    val selectedMood = remember { mutableIntStateOf(-1) }

    // Get the current date and time
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

    // Mood options and their scores
    data class MoodOption(val text: String, val value: Int, val image: Int)
    GradientBackground()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DetailAppBar(
            title = title,
            detail = "Today, $currentDateTime",
            onBackClick = {
                navController.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        val points = listOf(
            Point(0f, 2f), // Monday - Average
            Point(1f, 4f), // Tuesday - Excellent
            Point(2f, 1f), // Wednesday - Bad
            Point(3f, 3f), // Thursday - Good
            Point(4f, 0f), // Friday - Terrible
            Point(5f, 2f), // Saturday - Average
            Point(6f, 3f)  // Sunday - Good
        )

        MoodLineChartWithInteractivity(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            points = points
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun MoodEntryScreenPreview() {
    //MoodEntryScreen(onMoodSelected = { /* Preview only */ })
}
