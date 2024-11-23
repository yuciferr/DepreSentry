package com.example.depresentry.presentation.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.depresentry.presentation.composables.AnimatedBarChart
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.EnhancedCircularProgress
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.LocationData
import com.example.depresentry.presentation.composables.LocationInsights
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DetailedStatsScreen(navController: NavController, title: String) {
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

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
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (title) {
            "Steps" -> {
                var timeRange by remember { mutableStateOf("Weekly") }
                var currentWeekOffset by remember { mutableStateOf(0) }
                var animationProgress by remember { mutableStateOf(0f) }
                
                EnhancedCircularProgress(
                    progress = 0.75f,
                    steps = 7500,
                    goal = 10000,
                    calories = 320,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                WeeklyNavigation(
                    currentWeekOffset = currentWeekOffset,
                    onWeekChange = { currentWeekOffset = it },
                    timeRange = timeRange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("Weekly", "Monthly").forEach { range ->
                        Text(
                            text = range,
                            color = if (timeRange == range) Color(0xFFF9F775) else Color.White,
                            modifier = Modifier
                                .clickable { 
                                    timeRange = range
                                    currentWeekOffset = 0 // Görünüm değiştiğinde offset'i sıfırla
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = if (timeRange == range) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedBarChart(
                    data = if (timeRange == "Weekly")
                        listOf(6500f, 8200f, 7400f, 9100f, 8800f, 7900f, 7500f)
                    else
                        listOf(7200f, 6800f, 8500f, 7900f, 8200f, 7600f, 8900f, 
                              9100f, 8400f, 7800f, 8300f, 7700f, 8100f, 8600f),
                    maxValue = 10000f,
                    labels = if (timeRange == "Weekly")
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    else
                        (1..14).map { it.toString() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(250.dp),
                    onBarHeightChanged = { progress -> 
                        animationProgress = progress
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedContent(
                    targetState = calculateStats(timeRange, currentWeekOffset),
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
                    },
                    label = "Stats Animation"
                ) { stats ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            stat = StatItem(
                                title = "Daily Average",
                                value = "${stats.dailyAverage}",
                                unit = "steps"
                            )
                        )
                        StatisticItem(
                            stat = StatItem(
                                title = "Total Distance",
                                value = "${stats.totalDistance}",
                                unit = "km"
                            )
                        )
                        StatisticItem(
                            stat = StatItem(
                                title = "Calories",
                                value = "${stats.totalCalories}",
                                unit = "kcal"
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LocationInsights(
                    locations = listOf(
                        LocationData(
                            name = "Home",
                            timeSpent = "14h 30m",
                            steps = 2500,
                            icon = Icons.Default.Home
                        ),
                        LocationData(
                            name = "Office",
                            timeSpent = "8h 15m",
                            steps = 4200,
                            icon = Icons.Default.Email
                        ),
                        LocationData(
                            name = "Gym",
                            timeSpent = "1h 45m",
                            steps = 800,
                            icon = Icons.Default.ShoppingCart
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            "Sleep" -> {

            }
            "Screen Time" -> {

            }
            "Mood" -> {

            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatisticItem(stat: StatItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stat.value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = stat.unit,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = stat.title,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

private data class StatItem(
    val title: String,
    val value: String,
    val unit: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeeklyNavigation(
    currentWeekOffset: Int,
    onWeekChange: (Int) -> Unit,
    timeRange: String,
    modifier: Modifier = Modifier
) {
    var weekStartDate by remember { mutableStateOf("") }
    var weekEndDate by remember { mutableStateOf("") }
    
    LaunchedEffect(currentWeekOffset) {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.add(Calendar.WEEK_OF_YEAR, currentWeekOffset)
        
        // Haftanın başlangıç tarihi
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        weekStartDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time)
        
        // Haftanın bitiş tarihi
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        weekEndDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time)
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onWeekChange(currentWeekOffset - 1) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Week",
                tint = Color.White
            )
        }
        
        AnimatedContent(
            targetState = "$weekStartDate - $weekEndDate",
            transitionSpec = {
                slideInHorizontally { width -> width } + fadeIn() with
                slideOutHorizontally { width -> -width } + fadeOut()
            },
            label = "Date Range Animation"
        ) { dates ->
            Text(
                text = dates,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        IconButton(onClick = { onWeekChange(currentWeekOffset + 1) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Week",
                tint = Color.White
            )
        }
    }
}

// Stats veri sınıfı
data class StatsData(
    val dailyAverage: Int,
    val totalDistance: Float,
    val totalCalories: Int
)

// İstatistik hesaplama fonksiyonu
private fun calculateStats(timeRange: String, weekOffset: Int): StatsData {
    // Gerçek uygulamada bu veriler API'den veya veritabanından gelecek
    return when (timeRange) {
        "Weekly" -> when (weekOffset) {
            0 -> StatsData(
                dailyAverage = 9245,
                totalDistance = 42.5f,
                totalCalories = 2450
            )
            -1 -> StatsData(
                dailyAverage = 8750,
                totalDistance = 38.2f,
                totalCalories = 2300
            )
            1 -> StatsData(
                dailyAverage = 9500,
                totalDistance = 44.8f,
                totalCalories = 2600
            )
            else -> StatsData(
                dailyAverage = 8900,
                totalDistance = 40.0f,
                totalCalories = 2400
            )
        }
        else -> StatsData(
            dailyAverage = 8800,
            totalDistance = 165.5f,
            totalCalories = 9800
        )
    }
}
