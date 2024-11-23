package com.example.depresentry.presentation.composables

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun MoodLineChartPreview() {

}

@Composable
fun AnimatedBarChart(
    data: List<Float>,
    maxValue: Float,
    labels: List<String>,
    modifier: Modifier = Modifier,
    onBarHeightChanged: (Float) -> Unit = {}
) {
    var selectedBar by remember { mutableStateOf<Int?>(null) }
    val barWidth = 40.dp
    val barSpacing = 18.dp

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .width((barWidth + barSpacing) * data.size)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val barTotalWidth = barWidth.toPx() + barSpacing.toPx()
                        val index = (offset.x / barTotalWidth).toInt()
                        selectedBar = if (selectedBar == index) null else index
                    }
                }
        ) {
            val barTotalWidth = barWidth.toPx() + barSpacing.toPx()
            val availableHeight = size.height - 40.dp.toPx()

            data.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * availableHeight * animatedProgress.value
                val topLeft = Offset(
                    x = index * barTotalWidth,
                    y = availableHeight - barHeight
                )

                // Bar çizimi
                drawRoundRect(
                    color = Color(0xFFF9F775),
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(
                        width = barWidth.toPx(),
                        height = barHeight
                    ),
                    cornerRadius = CornerRadius(12f)
                )

                // Label çizimi
                drawContext.canvas.nativeCanvas.drawText(
                    labels[index],
                    topLeft.x + (barWidth.toPx() / 2),
                    size.height - 10.dp.toPx(),
                    Paint().apply {
                        color = android.graphics.Color.WHITE
                        textAlign = Paint.Align.CENTER
                        textSize = 12.sp.toPx()
                    }
                )

                // Tooltip
                if (selectedBar == index) {
                    val percentage = ((value / maxValue) * 100).toInt()
                    val stepsText = "${value.toInt()} steps"
                    val percentageText = "$percentage% completed"

                    // Tooltip arka planı
                    drawRoundRect(
                        color = Color(0xFF2D2D2D),
                        topLeft = Offset(
                            x = topLeft.x - 30.dp.toPx(),
                            y = topLeft.y - 60.dp.toPx()
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = 100.dp.toPx(),
                            height = 50.dp.toPx()
                        ),
                        cornerRadius = CornerRadius(8f)
                    )

                    // Tooltip ok işareti
                    val trianglePath = Path().apply {
                        moveTo(
                            topLeft.x + barWidth.toPx() / 2 - 8.dp.toPx(),
                            topLeft.y - 10.dp.toPx()
                        )
                        lineTo(
                            topLeft.x + barWidth.toPx() / 2 + 8.dp.toPx(),
                            topLeft.y - 10.dp.toPx()
                        )
                        lineTo(topLeft.x + barWidth.toPx() / 2, topLeft.y)
                        close()
                    }
                    drawPath(
                        path = trianglePath,
                        color = Color(0xFF2D2D2D)
                    )

                    // Tooltip metinleri
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            stepsText,
                            topLeft.x + (barWidth.toPx() / 2),
                            topLeft.y - 35.dp.toPx(),
                            Paint().apply {
                                color = android.graphics.Color.WHITE
                                textAlign = Paint.Align.CENTER
                                textSize = 12.sp.toPx()
                            }
                        )
                        drawText(
                            percentageText,
                            topLeft.x + (barWidth.toPx() / 2),
                            topLeft.y - 20.dp.toPx(),
                            Paint().apply {
                                color = android.graphics.Color.WHITE
                                textAlign = Paint.Align.CENTER
                                textSize = 12.sp.toPx()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedCircularProgress(
    progress: Float,
    steps: Int,
    goal: Int,
    calories: Int,
    modifier: Modifier = Modifier
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(progress) {
        animate(
            initialValue = animatedProgress,
            targetValue = progress,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        ) { value, _ -> animatedProgress = value }
    }

    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    Box(
        modifier = modifier
            .background(
                color = cardBackgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = cardStrokeColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Arka plan halkası
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawArc(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 25f, cap = StrokeCap.Round)
                    )

                    // Animasyonlu ilerleme halkası
                    drawArc(
                        color = Color(0xFFF9F775),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = 25f, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "$steps",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "steps",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% of daily goal",
                        fontSize = 14.sp,
                        color = Color(0xFFF9F775),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.Star,
                    value = "$calories",
                    label = "kcal",
                    color = Color(0xFFF9F775)
                )
                StatisticItem(
                    icon = Icons.Default.Check,
                    value = "$goal",
                    label = "Goal",
                    color = Color(0xFFF9F775)
                )
            }
        }
    }
}

@Composable
fun LocationInsights(
    locations: List<LocationData>,
    modifier: Modifier = Modifier
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Journey",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            locations.forEach { location ->
                LocationItem(
                    location = location,
                    color = Color(0xFFF9F775)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class LocationData(
    val name: String,
    val timeSpent: String,
    val steps: Int,
    val icon: ImageVector
)

@Composable
fun StatisticItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun LocationItem(
    location: LocationData,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = location.icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = location.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = location.timeSpent,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(0.4f)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = color.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${location.steps}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
