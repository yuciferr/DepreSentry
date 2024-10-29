package com.example.depresentry.presentation.mood

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depresentry.R
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.SurveyButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodEntryScreen(onMoodSelected: (Int) -> Unit) {
    // Remember the selected mood (1 to 5) based on button clicks
    val selectedMood = remember { mutableIntStateOf(-1) }

    // Get the current date and time
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

    // Mood options and their scores
    data class MoodOption(val text: String, val value: Int, val image: Int)

    val moodOptions = listOf(
        MoodOption("Excellent", 5, R.drawable.emoji_excellent),
        MoodOption("Good", 4, R.drawable.emoji_good),
        MoodOption("Average", 3, R.drawable.emoji_average),
        MoodOption("Bad", 2, R.drawable.emoji_bad),
        MoodOption("Terrible", 1, R.drawable.emoji_terrible)
    )

    GradientBackground()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DetailAppBar(
            title = "Mood",
            detail = "Today, $currentDateTime",
            onBackClick = {
                // /todo handle back navigation
            }
        )

        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "How was your mood today?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE3CCF2),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Display mood buttons
        moodOptions.forEach { moodOption ->
            SurveyButton(
                text = moodOption.text,
                painter = painterResource(id = moodOption.image),
                onClick = {
                    selectedMood.intValue = moodOption.value
                    // Navigate to the next screen or perform action
                    onMoodSelected(moodOption.value)
                })
        }
    }
}

@Preview
@Composable
fun MoodEntryScreenPreview() {
    MoodEntryScreen(onMoodSelected = { /* Preview only */ })
}
