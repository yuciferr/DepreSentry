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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.depresentry.presentation.composables.AppCategoryItem
import com.example.depresentry.presentation.composables.AppUsageDonutChart
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.EnhancedCircularProgress
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.LocationData
import com.example.depresentry.presentation.composables.LocationInsights
import com.example.depresentry.presentation.composables.MoodFactor
import com.example.depresentry.presentation.composables.MoodFactorsAnalysis
import com.example.depresentry.presentation.composables.MoodJournalSection
import com.example.depresentry.presentation.composables.MoodLineGraph
import com.example.depresentry.presentation.composables.SleepMetric
import com.example.depresentry.presentation.composables.SleepPhaseIndicator
import com.example.depresentry.presentation.composables.SleepQualityChart
import com.example.depresentry.presentation.composables.SleepTrendsSection
import com.example.depresentry.presentation.composables.TimeRangeSelector
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DetailedStatsScreen(navController: NavController, title: String) {
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

    GradientBackground()

    when (title) {
        "Steps" -> {
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
        }
        "Sleep" -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailAppBar(
                    title = title,
                    detail = "Today, $currentDateTime",
                    onBackClick = { navController.popBackStack() }
                )

                var selectedTimeRange by remember { mutableStateOf("Daily") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TimeRangeSelector(
                        selectedRange = selectedTimeRange,
                        onRangeSelected = { selectedTimeRange = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Uyku kalitesi grafiği
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        SleepQualityChart(
                            sleepData = listOf(
                                "Mon" to 0.8f,
                                "Tue" to 0.6f,
                                "Wed" to 0.9f,
                                "Thu" to 0.7f,
                                "Fri" to 0.85f,
                                "Sat" to 0.95f,
                                "Sun" to 0.75f
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            sleepPhases.forEach { (phase, hours, color) ->
                                SleepPhaseIndicator(phase, hours, color)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Metrik kartları için Grid yerine Row'lar kullanalım
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            sleepMetrics.take(2).forEach { metric ->
                                SleepMetricCard(
                                    metric = metric,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            sleepMetrics.takeLast(2).forEach { metric ->
                                SleepMetricCard(
                                    metric = metric,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SleepTrendsSection(selectedTimeRange)
                }
            }
        }
        "Screen Time" -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DetailAppBar(
                    title = title,
                    detail = "Today, $currentDateTime",
                    onBackClick = { navController.popBackStack() }
                )

                var selectedTimeRange by remember { mutableStateOf("Daily") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TimeRangeSelector(
                        selectedRange = selectedTimeRange,
                        onRangeSelected = { selectedTimeRange = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AppUsageDonutChart(
                            appUsageData = listOf(
                                Triple("Social", 4.5f, Color(0xFF4CAF50)),
                                Triple("Entertainment", 2.8f, Color(0xFFFFC107)),
                                Triple("Productivity", 1.7f, Color(0xFF2196F3))
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "9h 45m",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    appCategories.forEach { category ->
                        AppCategoryItem(
                            category = category.first,
                            duration = category.second,
                            color = category.third,
                            percentage = category.fourth
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        "Mood" -> {
            var selectedTimeRange by remember { mutableStateOf("Daily") }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DetailAppBar(
                    title = title,
                    detail = "Today, $currentDateTime",
                    onBackClick = { navController.popBackStack() }
                )
                TimeRangeSelector(selectedTimeRange) { selectedTimeRange = it }
                
                // Ruh hali grafiği ve etkileşimli noktalar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    MoodLineGraph(
                        moodData = listOf(
                            "Mon" to 75,
                            "Tue" to 60,
                            "Wed" to 85,
                            "Thu" to 70,
                            "Fri" to 90,
                            "Sat" to 95,
                            "Sun" to 80
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Faktör analizi
                MoodFactorsAnalysis(
                    factors = listOf(
                        MoodFactor("Sleep", 0.8f, Icons.Default.Notifications),
                        MoodFactor("Exercise", 0.6f, Icons.Default.LocationOn),
                        MoodFactor("Social", 0.9f, Icons.Default.Person),
                        MoodFactor("Work", 0.4f, Icons.Default.Call)
                    )
                )
                
                // Günlük notlar ve aktiviteler
                if (selectedTimeRange == "Daily") {
                    MoodJournalSection(
                        activities = listOf("Walking", "Reading", "Meeting"),
                        notes = "Felt energetic after morning exercise"
                    )
                }
            }
        }
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

// Screen Time için mock data
private val appCategories = listOf(
    Quadruple("Social Media", "4h 30m", Color(0xFF4CAF50), 0.45f),
    Quadruple("Entertainment", "2h 45m", Color(0xFFFFC107), 0.28f),
    Quadruple("Productivity", "1h 40m", Color(0xFF2196F3), 0.17f),
    Quadruple("Others", "50m", Color(0xFFFF5722), 0.10f)
)

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

// Mood için mock data
private fun showMoodDetails(moodData: Pair<String, Int>) {
    // TODO: Implement mood details dialog
}

// Sleep için mock data
private val sleepPhases = listOf(
    Triple("Deep Sleep", 2.5f, Color(0xFF4CAF50)),
    Triple("Light Sleep", 4.0f, Color(0xFFFFC107)),
    Triple("REM", 1.5f, Color(0xFF2196F3))
)

private val sleepMetrics = listOf(
    SleepMetric("Total Sleep", "8h 15m", Icons.Default.Notifications),
    SleepMetric("Sleep Score", "85", Icons.Default.Star),
    SleepMetric("Bedtime", "23:30", Icons.Default.Info),
    SleepMetric("Wake Time", "07:45", Icons.Default.Info)
)

// Yeni eklenen composable
@Composable
private fun SleepMetricCard(
    metric: SleepMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
        border = BorderStroke(1.dp, Color(0xFF806691))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = Color(0xFFF9F775),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = metric.title,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = metric.value,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
