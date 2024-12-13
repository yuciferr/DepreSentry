package com.example.depresentry.presentation.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.PI
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CircularTimePicker(
    startTime: LocalTime,
    endTime: LocalTime,
    onStartTimeChanged: (LocalTime) -> Unit,
    onEndTimeChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDraggingStart by remember { mutableStateOf(false) }
    var isDraggingEnd by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Hangi noktaya daha yakın olduğuna göre başlangıç veya bitiş zamanını seç
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val startAngle = getTimeAngle(startTime)
                            val endAngle = getTimeAngle(endTime)
                            val currentAngle = getAngleFromOffset(offset, center)
                            
                            val startDiff = angleDifference(currentAngle, startAngle)
                            val endDiff = angleDifference(currentAngle, endAngle)
                            
                            isDraggingStart = startDiff < endDiff
                            isDraggingEnd = !isDraggingStart
                        },
                        onDrag = { change, _ ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val angle = getAngleFromOffset(change.position, center)
                            val newTime = getTimeFromAngle(angle)
                            
                            if (isDraggingStart) {
                                onStartTimeChanged(newTime)
                            } else if (isDraggingEnd) {
                                onEndTimeChanged(newTime)
                            }
                        },
                        onDragEnd = {
                            isDraggingStart = false
                            isDraggingEnd = false
                        }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) / 2f - 32.dp.toPx()

            // Arka plan çemberi
            drawCircle(
                color = Color(0x23E8D1F7),
                radius = radius,
                style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
            )

            // Uyku süresi arkı
            val startAngle = getTimeAngle(startTime)
            val endAngle = getTimeAngle(endTime)
            var sweepAngle = endAngle - startAngle
            if (sweepAngle < 0) sweepAngle += 360f

            drawArc(
                color = Color(0xFF5A3472),
                startAngle = startAngle - 90,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
            )

            // Başlangıç noktası işareti
            val startX = center.x + radius * kotlin.math.cos((startAngle - 90) * PI / 180f).toFloat()
            val startY = center.y + radius * kotlin.math.sin((startAngle - 90) * PI / 180f).toFloat()
            drawCircle(
                color = Color(0x23E8D1F7),
                radius = 18.dp.toPx(),
                center = Offset(startX, startY)
            )

            // Bitiş noktası işareti
            val endX = center.x + radius * kotlin.math.cos((endAngle - 90) * PI / 180f).toFloat()
            val endY = center.y + radius * kotlin.math.sin((endAngle - 90) * PI / 180f).toFloat()
            drawCircle(
                color = Color(0xFF5A3472),
                radius = 18.dp.toPx(),
                center = Offset(endX, endY)
            )
        }

        // Zaman göstergeleri
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "to",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getTimeAngle(time: LocalTime): Float {
    return (time.hour * 60f + time.minute) / (24f * 60f) * 360f
}

private fun getAngleFromOffset(offset: Offset, center: Offset): Float {
    val angle = atan2(offset.y - center.y, offset.x - center.x) * 180f / PI.toFloat()
    return (angle + 90f + 360f) % 360f
}

private fun getTimeFromAngle(angle: Float): LocalTime {
    val totalMinutes = (angle / 360f * 24f * 60f).toInt()
    val hours = (totalMinutes / 60) % 24
    val minutes = totalMinutes % 60
    return LocalTime.of(hours, minutes)
}

private fun angleDifference(angle1: Float, angle2: Float): Float {
    var diff = Math.abs(angle1 - angle2)
    if (diff > 180) diff = 360 - diff
    return diff
} 