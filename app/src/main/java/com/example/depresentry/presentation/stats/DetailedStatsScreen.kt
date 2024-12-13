package com.example.depresentry.presentation.stats

import android.content.Intent
import android.provider.Settings
import android.util.Log
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.depresentry.domain.model.Sleep
import com.example.depresentry.presentation.composables.AnimatedBarChart
import com.example.depresentry.presentation.composables.AppCategoryItem
import com.example.depresentry.presentation.composables.AppUsageDonutChart
import com.example.depresentry.presentation.composables.DetailAppBar
import com.example.depresentry.presentation.composables.EnhancedCircularProgress
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.MoodFactor
import com.example.depresentry.presentation.composables.MoodFactorsAnalysis
import com.example.depresentry.presentation.composables.MoodJournalSection
import com.example.depresentry.presentation.composables.MoodLineGraph
import com.example.depresentry.presentation.composables.TimeRangeSelector
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.util.AppNameFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DetailedStatsScreen(
    navController: NavController, 
    title: String,
    viewModel: DetailedStatsViewModel = hiltViewModel()
) {
    LaunchedEffect(title) {
        Log.d("DetailedStatsScreen", "Screen launched with title: $title")
        viewModel.loadDataForScreen(title)
    }
    
    val currentDateTime = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())
    val context = LocalContext.current

    GradientBackground()
    
    Column(modifier = Modifier.fillMaxSize().padding(top=16.dp)) {
        DetailAppBar(
            title = title,
            detail = "Today, $currentDateTime",
            onBackClick = { navController.popBackStack() }
        )

        when (title) {
            "Steps" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var timeRange by remember { mutableStateOf("Weekly") }
                    var currentWeekOffset by remember { mutableStateOf(0) }
                    var animationProgress by remember { mutableStateOf(0f) }
                    
                    EnhancedCircularProgress(
                        progress = (viewModel.dailySteps.value?.steps?.toFloat() ?: 0f) / 10000f,
                        steps = viewModel.dailySteps.value?.steps ?: 0,
                        goal = 10000,
                        calories = viewModel.dailySteps.value?.burnedCalorie ?: 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

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
                                        currentWeekOffset = 0
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = if (timeRange == range) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val chartData = if (timeRange == "Weekly") {
                        viewModel.weeklySteps.value.map { it.steps.toFloat() }
                    } else {
                        viewModel.monthlySteps.value.map { it.steps.toFloat() }
                    }
                    
                    val labels = if (timeRange == "Weekly") {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    } else {
                        val today = LocalDate.now()
                        (0 until viewModel.monthlySteps.value.size).map { index ->
                            val date = today.minusDays(index.toLong())
                            date.format(DateTimeFormatter.ofPattern("MMM d"))
                        }.reversed()
                    }
                    
                    AnimatedBarChart(
                        data = chartData,
                        maxValue = 10000f,
                        labels = labels,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(250.dp),
                        onBarHeightChanged = { progress -> 
                            animationProgress = progress
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val currentSteps = if (timeRange == "Weekly") {
                        viewModel.weeklySteps.value
                    } else {
                        viewModel.monthlySteps.value
                    }
                    
                    val dailyAverage = currentSteps.map { it.steps }.average().toInt()
                    val totalDistance = currentSteps.sumOf { it.steps } * 0.0008
                    val totalCalories = currentSteps.sumOf { it.burnedCalorie }
                    
                    AnimatedContent(
                        targetState = StatsData(
                            dailyAverage = dailyAverage,
                            totalDistance = totalDistance.toFloat(),
                            totalCalories = totalCalories
                        ),
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
                                    value = String.format("%.1f", stats.totalDistance),
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
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            "Sleep" -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var selectedTimeRange by remember { mutableStateOf("Daily") }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TimeRangeSelector(
                                selectedRange = selectedTimeRange,
                                onRangeSelected = { selectedTimeRange = it }
                            )
                            
                            IconButton(
                                onClick = { 
                                    navController.navigate(MainScreen.SleepEntry.route)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp)
                                    .size(40.dp)
                                    .background(
                                        color = Color(0x33FFFFFF),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Sleep Entry",
                                    tint = Color(0xFFF9F775)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        when (selectedTimeRange) {
                            "Daily" -> {
                                val dailySleep = viewModel.dailySleep.value
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
                                    border = BorderStroke(1.dp, Color(0xFF806691))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(8.dp)
                                        ) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawCircle(
                                                    color = Color(0x40E8D1F7),
                                                    radius = size.minDimension / 2,
                                                    style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round)
                                                )
                                                
                                                val duration = dailySleep?.duration ?: 0.0
                                                val sweepAngle = (duration / 24.0 * 360).toFloat()
                                                
                                                drawArc(
                                                    color = Color(0xFF5A3472),
                                                    startAngle = -90f,
                                                    sweepAngle = sweepAngle,
                                                    useCenter = false,
                                                    style = Stroke(width = 25.dp.toPx(), cap = StrokeCap.Round)
                                                )
                                            }
                                            
                                            Column(
                                                modifier = Modifier.align(Alignment.Center),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                val duration = dailySleep?.duration ?: 0.0
                                                val hours = duration.toInt()
                                                val minutes = ((duration - hours) * 60).toInt()
                                                
                                                Text(
                                                    text = "${hours}h ${minutes}m",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Total Sleep",
                                                    fontSize = 14.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dailySleep?.sleepStartTime ?: "--:--",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Bedtime",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dailySleep?.sleepEndTime ?: "--:--",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Wake up",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {
                                val sleepData = if (selectedTimeRange == "Weekly") {
                                    viewModel.weeklySleep.value
                                } else {
                                    viewModel.monthlySleep.value
                                }
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
                                    border = BorderStroke(1.dp, Color(0xFF806691))
                                ) {
                                    SleepLineChart(
                                        sleepData = sleepData,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
                                border = BorderStroke(1.dp, Color(0xFF806691))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = viewModel.dailySleep.value?.quality ?: "N/A",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Sleep Quality",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
                                border = BorderStroke(1.dp, Color(0xFF806691))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val avgDuration = when (selectedTimeRange) {
                                        "Weekly" -> viewModel.weeklySleep.value.map { it.duration }.average()
                                        "Monthly" -> viewModel.monthlySleep.value.map { it.duration }.average()
                                        else -> viewModel.dailySleep.value?.duration ?: 0.0
                                    }
                                    val hours = avgDuration.toInt()
                                    val minutes = ((avgDuration - hours) * 60).toInt()
                                    
                                    Text(
                                        text = "${hours}h ${minutes}m",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Average Duration",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            "Screen Time" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    LaunchedEffect(Unit) {
                        viewModel.checkUsagePermissionAndLoadStats()
                    }

                    if (!viewModel.hasUsagePermission.value) {
                        PermissionRequest(
                            message = "Uygulama kullanım istatistiklerini görüntülemek için lütfen izin verin",
                            onRequestPermission = {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        )
                    } else {
                        var selectedTimeRange by remember { mutableStateOf("Daily") }
                        val stats = when (selectedTimeRange) {
                            "Daily" -> viewModel.dailyUsageStats.value
                            "Weekly" -> viewModel.weeklyUsageStats.value
                            "Monthly" -> viewModel.monthlyUsageStats.value
                            else -> emptyMap()
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            TimeRangeSelector(
                                selectedRange = selectedTimeRange,
                                onRangeSelected = { selectedTimeRange = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (stats.isNotEmpty()) {
                                val totalTime = stats.values.sum()
                                val categoryStats = stats.groupByCategory()

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                ) {
                                    AppUsageDonutChart(
                                        appUsageData = categoryStats.toDonutChartData(),
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    Column(
                                        modifier = Modifier.align(Alignment.Center),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = viewModel.formatDuration(totalTime),
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Total ${selectedTimeRange.lowercase()} usage",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Most Used Apps",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // En çok kullanılan 5 uygulamayı listele
                                stats.entries.take(5).forEach { (appName, duration) ->
                                    val formattedName = AppNameFormatter.formatAppName(appName)
                                    val category = AppNameFormatter.getCategoryForApp(appName)

                                    AppCategoryItem(
                                        category = formattedName,
                                        duration = viewModel.formatDuration(duration),
                                        color = category.color,
                                        percentage = (duration.toFloat() / totalTime)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                if (selectedTimeRange != "Daily") {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Average daily usage: ${viewModel.formatDuration(totalTime / when(selectedTimeRange) {
                                            "Weekly" -> 7
                                            "Monthly" -> 30
                                            else -> 1
                                        })}",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                // Veri yoksa mesaj göster
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No app usage data available for $selectedTimeRange view",
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
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

@Composable
private fun PermissionRequest(
    message: String,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE)
            )
        ) {
            Text("Grant Permission")
        }
    }
}

// Stats map'ini kategorilere bölen extension function
private fun Map<String, Long>.groupByCategory(): Map<AppNameFormatter.AppCategory, Long> {
    return entries.groupBy(
        keySelector = { (appName, _) -> AppNameFormatter.getCategoryForApp(appName) },
        valueTransform = { it.value }
    ).mapValues { (_, durations) -> durations.sum() }
}

// Donut chart için veriyi hazırlayan fonksiyon
private fun Map<AppNameFormatter.AppCategory, Long>.toDonutChartData(): List<Triple<String, Float, Color>> {
    return entries.map { (category, duration) ->
        Triple(
            category.categoryName,
            duration / (1000f * 60f * 60f), // Saate çevir
            category.color
        )
    }
}

@Composable
private fun SleepLineChart(
    sleepData: List<Sleep>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val maxDuration = 24f // Maksimum 24 saat
            
            // Yatay çizgiler ve etiketler için
            val horizontalLines = 6
            val verticalSpace = height / horizontalLines
            
            // Yatay çizgiler
            for (i in 0..horizontalLines) {
                val y = height - (i * verticalSpace)
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            if (sleepData.isNotEmpty()) {
                val points = sleepData.mapIndexed { index, sleep ->
                    val x = width * index / (sleepData.size - 1)
                    val y = height - (height * (sleep.duration / maxDuration)).toFloat()
                    Offset(x, y)
                }
                
                // Çizgi çizimi
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color(0xFFFFFBA5),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 4.dp.toPx()
                    )
                }
                
                // Noktalar
                points.forEach { point ->
                    drawCircle(
                        color = Color(0xFFFFF89A),
                        radius = 5.dp.toPx(),
                        center = point
                    )
                }
            }
        }
        
        // Y ekseni etiketleri
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 6 downTo 0) {
                Text(
                    text = "${i * 4}h",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
