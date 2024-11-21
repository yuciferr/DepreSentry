package com.example.depresentry.presentation.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depresentry.R

@Composable
fun CircularProgress(
    progress: Float,
    color: Color,
    size: Dp = 80.dp,
    showNumber: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Animate the progress value
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = "" // Adjust duration as needed
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = (size / 10).toPx()
            drawArc(
                color = Color.Gray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f, // Use animated progress
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        if (showNumber) {
            Text(
                text = "${(animatedProgress * 100).toInt()}",
                color = Color.White,
                fontSize = (size / 4).value.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun BarChart(
    data: List<Int>,
    modifier: Modifier = Modifier,
    barColor: Color,
    labelColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom // Ensures all columns align to the bottom
    ) {
        val maxData = data.maxOrNull()?.toFloat() ?: 1f

        data.forEachIndexed { index, value ->
            val animatedHeight by animateDpAsState(
                targetValue = (value / maxData * 40).dp, // Animate to the calculated height
                animationSpec = tween(durationMillis = 1000), label = "" // Set animation duration
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight() // Makes all columns the same height
            ) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(animatedHeight) // Use animated height for smooth increase
                        .background(barColor, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf("M", "T", "W", "T", "F", "S", "S")[index],
                    color = labelColor,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Centers label
                )
            }
        }
    }
}



@Composable
fun DailyCard(
    score: Int,
    message: String,
    message2: String,
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {}
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onIconClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgress(
                progress = score / 100f,
                color = Color(0xFF00FF80),
                size = 100.dp,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = message2,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ActivityCard(
    title: String,
    value: Int,
    unit: String,
    targetValue: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {}
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)
    val progress = value / targetValue.toFloat()

    Card(
        modifier = modifier
            .padding(vertical = 3.dp).clickable { onIconClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(modifier = Modifier
            .padding(16.dp)
            .height(120.dp)
            .fillMaxWidth()) {
            // Title in the top-left
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.TopStart)
            )


            // Numeric value in the bottom-left
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "$value",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            // Circular progress in the bottom-right
            CircularProgress(
                progress = progress,
                color = color,
                showNumber = false,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}


@Composable
fun StatsCard(
    title: String,
    stats: List<Pair<String, Int>>, // Example: [("Whatsapp", 157), ("Instagram", 72)]
    weeklyData: List<Int>, // Weekly usage data for each app, e.g., [[2, 3, 4, 2, 3, 4, 1], [1, 2, 3, 4, 2, 1, 3]]
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFAA66CC),
    onIconClick: () -> Unit = {}
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp).clickable { onIconClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title at the top
            Text(
                text = title,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    stats.forEach { (label, value) ->
                        // App icon and name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp, end = 8.dp)
                        ) {
                            // Load the icon dynamically from R.drawable based on label name
                            val iconResId = context.resources.getIdentifier(label.lowercase(), "drawable", context.packageName)
                            if (iconResId != 0) {
                                Icon(
                                    painter = painterResource(id = iconResId),
                                    contentDescription = label,
                                    tint = Color.Unspecified, // Keeps original icon color
                                    modifier = Modifier.size(22.dp) // Ensure consistent icon size
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle, // Fallback icon
                                    contentDescription = label,
                                    tint = Color(0xFF00FF80),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                color = Color(0xFFE3B8FF),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${value / 60}h ${value % 60}min",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                // Weekly Bar Chart
                BarChart(
                    data = weeklyData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 8.dp),
                    barColor = color,
                    labelColor = textColor
                )
            }
        }
    }
}



@Preview
@Composable
fun PreviewCircularProgress() {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        DailyCard(
            score = 84,
            message = "Your mental health score is improving, you're on the right track!",
            message2 = "Consider reaching out to a friend for a quick chat or coffee."
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActivityCard(
                title = "Steps",
                value = 3124,
                unit = "Steps",
                color = Color(0xFFCB6589),
                targetValue = 10000,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActivityCard(
                title = "Sleep",
                value = 6,
                unit = "Hours",
                color = Color(0xFFE2E06A),
                targetValue = 8,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        StatsCard(
            title = "Screen Time",
            stats = listOf("WhatsApp" to 157, "Youtube" to 72),
            weeklyData = listOf(2, 3, 4, 2, 3, 4, 1)

        )
        StatsCard(
            title = "Mood",
            stats = listOf("Excellent" to 157),
            weeklyData = listOf(2, 4, 3, 5, 3, 4, 1)

        )
    }
}