package com.example.depresentry.presentation.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SleepQualityChart(
    sleepData: List<Pair<String, Float>>, // Saat ve kalite puanı
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(sleepData) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1500)
        ) { value, _ ->
            animationProgress = value
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val lineSpacing = width / (sleepData.size - 1)
        
        val path = Path()
        val points = sleepData.mapIndexed { index, (_, quality) ->
            Offset(
                x = index * lineSpacing,
                y = height - (quality * height * animationProgress)
            )
        }
        
        // Çizgi grafiği çiz
        path.moveTo(points.first().x, points.first().y)
        points.forEach { point ->
            path.lineTo(point.x, point.y)
        }
        
        drawPath(
            path = path,
            color = Color(0xFFF9F775),
            style = Stroke(
                width = 4f,
                cap = StrokeCap.Round
            )
        )
        
        // Noktaları çiz
        points.forEach { point ->
            drawCircle(
                color = Color(0xFFF9F775),
                radius = 6f,
                center = point
            )
        }
    }
}

@Composable
fun MoodLineGraph(
    moodData: List<Pair<String, Int>>, // Tarih ve ruh hali puanı
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(moodData) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1500)
        ) { value, _ ->
            animationProgress = value
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val lineSpacing = width / (moodData.size - 1)
        
        val path = Path()
        val points = moodData.mapIndexed { index, (_, mood) ->
            Offset(
                x = index * lineSpacing,
                y = height - (mood / 100f * height * animationProgress)
            )
        }
        
        // Gradient arka plan
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                    Color.Transparent
                )
            )
        )
        
        // Çizgi grafiği çiz
        path.moveTo(points.first().x, points.first().y)
        points.forEach { point ->
            path.lineTo(point.x, point.y)
        }
        
        drawPath(
            path = path,
            color = Color(0xFF4CAF50),
            style = Stroke(
                width = 4f,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun AppUsageDonutChart(
    appUsageData: List<Triple<String, Float, Color>>, // App adı, kullanım süresi (saat), renk
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(appUsageData) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1500)
        ) { value, _ ->
            animationProgress = value
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = minOf(width, height) / 2
        val centerX = width / 2
        val centerY = height / 2
        
        var startAngle = 0f
        val total = appUsageData.sumOf { it.second.toDouble() }.toFloat()
        
        appUsageData.forEach { (_, usage, color) ->
            val sweepAngle = (usage / total) * 360f * animationProgress
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}

@Composable
fun AppCategoryItem(
    category: String,
    duration: String,
    color: Color,
    percentage: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = category,
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = duration,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
        
        Text(
            text = "${(percentage * 100).toInt()}%",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SleepPhaseIndicator(
    phase: String,
    hours: Float,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$phase ${hours}h",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MoodFactorsAnalysis(
    factors: List<MoodFactor>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Impact Factors",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        factors.forEach { factor ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = factor.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = factor.name,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    LinearProgressIndicator(
                        progress = factor.impact,
                        color = Color(0xFFF9F775),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
                
                Text(
                    text = "${(factor.impact * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class MoodFactor(
    val name: String,
    val impact: Float,
    val icon: ImageVector
)

data class SleepMetric(
    val title: String,
    val value: String,
    val icon: ImageVector
)

@Composable
fun TimeRangeSelector(
    selectedRange: String,
    ranges: List<String> = listOf("Daily", "Weekly", "Monthly"),
    onRangeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        ranges.forEach { range ->
            Text(
                text = range,
                color = if (selectedRange == range) Color(0xFFF9F775) else Color.White,
                fontSize = 16.sp,
                fontWeight = if (selectedRange == range) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clickable { onRangeSelected(range) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SleepMetricsGrid(metrics: List<SleepMetric>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(metrics) { metric ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
    }
}

@Composable
fun SleepTrendsSection(timeRange: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Sleep Trends",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SleepTrendCard(
                title = "Average Sleep Time",
                value = "23:30",
                trend = "+30min",
                isPositive = true
            )
            SleepTrendCard(
                title = "Average Wake Time",
                value = "07:45",
                trend = "-15min",
                isPositive = false
            )
        }
    }
}

@Composable
private fun SleepTrendCard(
    title: String,
    value: String,
    trend: String,
    isPositive: Boolean
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
        border = BorderStroke(1.dp, Color(0xFF806691))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = trend,
                color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFE57373),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodJournalSection(
    activities: List<String>,
    notes: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x23E8D1F7)),
        border = BorderStroke(1.dp, Color(0xFF806691))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Journal",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activities.forEach { activity ->
                    ActivityChip(activity)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = notes,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ActivityChip(activity: String) {
    Surface(
        color = Color(0xFF806691),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = activity,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
} 