package com.example.depresentry.presentation.calendar

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.Calendar
import com.example.depresentry.presentation.composables.CircularProgress
import com.example.depresentry.presentation.composables.GradientBackground
import java.time.LocalDate
import android.util.Log


@Composable
fun CalendarScreen(
    navController: NavHostController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    // Calendar için depression score map'i
    val moodData = remember(uiState.monthlyData) {
        uiState.monthlyData.associate { dailyData ->
            LocalDate.parse(dailyData.date) to dailyData.depressionScore
        }
    }

    GradientBackground()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.Transparent,
    ) { contentPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                // Calendar
                item {
                    Calendar(
                        currentMonth = uiState.currentYearMonth,
                        initialSelectedDate = LocalDate.now(), // Today seçili olarak başla
                        moodData = moodData,
                        onDateSelected = viewModel::onDateSelected,
                        onMonthChanged = viewModel::onMonthChanged
                    )
                }

                // Today's Tasks Section (sadece bugün seçiliyken görünür)
                if (uiState.selectedDate == LocalDate.now() && uiState.todayTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Today's Tasks",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color(0xFFE3CCF2),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.todayTasks) { task ->
                        SwipeableTodoCard(
                            icon = Icons.Default.CheckCircle,
                            todoText = task.title,
                            status = task.status,
                            detail = task.body,
                            isToday = true,
                            onStatusChange = { }
                        )
                    }

                    // Ayırıcı
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }
                }

                // Seçili gün için veri yoksa mesaj
                if (uiState.selectedDate != null && uiState.selectedDayData == null) {
                    item {
                        Text(
                            text = "No data available for selected date",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color(0xFFE3CCF2),
                            fontSize = 16.sp
                        )
                    }
                }

                // Selected Day Summary
                uiState.selectedDayData?.let { dailyData ->
                    item {
                        DaySummaryCard(
                            daySummary = DaySummary(
                                depressionScore = dailyData.depressionScore,
                                enteredMood = getMoodText(dailyData.mood),
                                steps = dailyData.steps.steps,
                                sleepTime = String.format("%.1fh", dailyData.sleep.duration),
                                screenTime = String.format("%.1fh", dailyData.screenTime.total)
                            ),
                            selectedDate = uiState.selectedDate!!,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Selected Day Tasks (bugün değilse)
                uiState.selectedDayTasks?.let { tasks ->
                    if (uiState.selectedDate != LocalDate.now()) {
                        items(tasks) { task ->
                            SwipeableTodoCard(
                                icon = Icons.Default.CheckCircle,
                                todoText = task.title,
                                status = task.status,
                                detail = task.body,
                                isToday = false,
                                onStatusChange = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getMoodText(mood: Int): String {
    return when (mood) {
        1 -> "Terrible"
        2 -> "Bad"
        3 -> "Average"
        4 -> "Good"
        5 -> "Excellent"
        else -> "Unknown"
    }
}

data class DaySummary(
    val depressionScore: Int,
    val enteredMood: String,
    val steps: Int,
    val sleepTime: String,
    val screenTime: String
)

@Composable
fun DaySummaryCard(
    daySummary: DaySummary,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { /* Action on card click */ },
        colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
        border = BorderStroke(1.dp, Color(0xFF806691)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // Mood Score and Entered Mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mood Score
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgress(
                        progress = daySummary.depressionScore / 100f,
                        color = getMoodColor(daySummary.depressionScore),
                        size = 100.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mood Score",
                        color = Color(0xFFE3CCF2),
                        fontSize = 14.sp
                    )
                }


                // Entered Mood
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    // Selected Date
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, Color(0xFF806691), shape = RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedDate.dayOfMonth.toString(),
                                color = Color(0xFFE3CCF2),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedDate.month.name.substring(0, 3).uppercase(),
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Entered Mood
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = daySummary.enteredMood,
                            color = Color(0xFFE3CCF2),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current Mood",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Activity Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Steps
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgress(
                        progress = (daySummary.steps / 10000f).coerceIn(0f, 1f),
                        color = Color(0xFF4CAF50),
                        size = 70.dp,
                        showNumber = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${daySummary.steps}",
                        color = Color(0xFFE3CCF2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Steps",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Sleep Time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgress(
                        progress = getSleepProgress(daySummary.sleepTime),
                        color = Color(0xFF2196F3),
                        size = 70.dp,
                        showNumber = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = daySummary.sleepTime,
                        color = Color(0xFFE3CCF2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sleep",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Screen Time
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgress(
                        progress = getScreenTimeProgress(daySummary.screenTime),
                        color = Color(0xFFFFC107),
                        size = 70.dp,
                        showNumber = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = daySummary.screenTime,
                        color = Color(0xFFE3CCF2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Screen Time",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


// Utility functions for progress calculations
private fun getMoodColor(score: Int): Color {
    return when {
        score <= 33 -> Color(0xFFFF7043) // Soft red-orange tones for lower scores
        score <= 66 -> Color(0xFFFFA726) // Soft yellow-orange tones for medium scores
        else -> Color(0xFF8BC34A) // Olive green tones for higher scores
    }
}

private fun getSleepProgress(sleepTime: String): Float {
    // Parse sleep time like "7h 30m" and convert to progress (assuming 8 hours is ideal)
    val hours = sleepTime.substringBefore("h").toFloatOrNull() ?: 0f
    val minutes = sleepTime.substringAfter("h ").substringBefore("m").toFloatOrNull() ?: 0f
    val totalHours = hours + (minutes / 60f)
    return (totalHours / 8f).coerceIn(0f, 1f)
}

private fun getScreenTimeProgress(screenTime: String): Float {
    // Parse screen time like "3h 15m" and convert to progress (assuming 6 hours is max)
    val hours = screenTime.substringBefore("h").toFloatOrNull() ?: 0f
    val minutes = screenTime.substringAfter("h ").substringBefore("m").toFloatOrNull() ?: 0f
    val totalHours = hours + (minutes / 60f)
    return (totalHours / 6f).coerceIn(0f, 1f)
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableTodoCard(
    icon: ImageVector,
    todoText: String,
    status: String,
    detail: String,
    isToday: Boolean,
    onStatusChange: (String) -> Unit
) {

    val swipeableState = rememberSwipeableState(initialValue = 0)
    val width = with(LocalDensity.current) { 200.dp.toPx() }
    val cardOffset by animateDpAsState(
        targetValue = if (swipeableState.currentValue == 1) (-width / LocalDensity.current.density).dp else 0.dp,
        label = ""
    )

    var isExpanded by remember { mutableStateOf(false) }
    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) 120.dp else 72.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(cardHeight)
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {

        if (isToday) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {

                    onStatusChange("Done")
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Mark as Done", tint = Color.Green)
                }
                IconButton(onClick = {

                    onStatusChange("In Progress")
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Mark as In Progress", tint = Color.Yellow)
                }
                IconButton(onClick = {

                    onStatusChange("Pending")
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Mark as Pending", tint = Color.Red)
                }
            }
        }

        // Swipeable ve genişleyebilir kart
        Box(
            modifier = Modifier
                .offset(x = cardOffset)
                .fillMaxWidth()
                .swipeable(
                    state = swipeableState,
                    anchors = mapOf(0f to 0, -width to 1),
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                )
                .background(
                    color = if (isToday) Color(0xFF5C6BC0) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { isExpanded = !isExpanded }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Todo Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = todoText,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = status,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }

                // Detay sadece genişlediğinde görünecek
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detail,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}