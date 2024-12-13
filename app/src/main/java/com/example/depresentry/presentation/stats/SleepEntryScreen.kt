package com.example.depresentry.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.depresentry.presentation.composables.CircularTimePicker
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.GradientBackground
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun SleepEntryScreen(
    navController: NavController,
    viewModel: SleepEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

    GradientBackground()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DetailAppBar(
            title = "Sleep Entry",
            detail = "Today, $currentDateTime",
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Circular Time Picker
        CircularTimePicker(
            startTime = uiState.startTime,
            endTime = uiState.endTime,
            onStartTimeChanged = viewModel::onStartTimeChanged,
            onEndTimeChanged = viewModel::onEndTimeChanged,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sleep Quality Section
        Text(
            text = "Sleep Quality",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SleepQualityOption("Poor", uiState.quality == "Poor") {
                viewModel.onQualityChanged("Poor")
            }
            SleepQualityOption("Fair", uiState.quality == "Fair") {
                viewModel.onQualityChanged("Fair")
            }
            SleepQualityOption("Good", uiState.quality == "Good") {
                viewModel.onQualityChanged("Good")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        DSBasicButton(
            onClick = {
                viewModel.saveSleepData()
                navController.popBackStack()
            },
            buttonText = "Save Sleep Data"
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SleepQualityOption(
    quality: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Button(
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFD6BBFB) else Color(0x33FFFFFF),
            contentColor = if (isSelected) Color(0xFF3B255A) else Color(0xFFE3CCF2)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = quality,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
} 