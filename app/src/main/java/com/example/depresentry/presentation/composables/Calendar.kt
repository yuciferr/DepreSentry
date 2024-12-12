package com.example.depresentry.presentation.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun Calendar(
    currentMonth: YearMonth = YearMonth.now(),
    initialSelectedDate: LocalDate? = null,
    moodData: Map<LocalDate, Int> = emptyMap(),
    onDateSelected: (LocalDate) -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val headerTextColor = Color(0xFFE3CCF2)
    val selectedDayStrokeColor = Color(0xFFFFFFFF)
    val daysOfWeekBackgroundColor = Color(0xFF806691).copy(alpha = 0.2f)

    var displayedMonth by remember { mutableStateOf(currentMonth) }
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }

    LaunchedEffect(currentMonth) {
        displayedMonth = currentMonth
    }

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val daysInMonth = displayedMonth.lengthOfMonth()
    val startDayOfWeek = displayedMonth.atDay(1).dayOfWeek.value % 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 16.dp, horizontal = 4.dp)
    ) {
        // Header row with month and navigation arrows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { 
                displayedMonth = displayedMonth.minusMonths(1)
                onMonthChanged(displayedMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = headerTextColor
                )
            }

            Text(
                text = displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                color = headerTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(onClick = { 
                displayedMonth = displayedMonth.plusMonths(1)
                onMonthChanged(displayedMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = headerTextColor
                )
            }
        }

        // Days of week row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(daysOfWeekBackgroundColor)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = selectedDayStrokeColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Days of month grid (7x5)
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            var dayCounter = 1
            for (week in 0 until 5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (dayOfWeek in 0 until 7) {
                        val day = if (week == 0 && dayOfWeek < startDayOfWeek || dayCounter > daysInMonth) {
                            null
                        } else {
                            dayCounter++
                        }

                        val date = day?.let { LocalDate.of(displayedMonth.year, displayedMonth.month, it) }
                        val score = date?.let { moodData[it] }
                        DayBox(
                            day = day,
                            isSelected = selectedDate?.dayOfMonth == day && selectedDate?.month == displayedMonth.month,
                            score = score,
                            onClick = {
                                day?.let {
                                    val newDate = LocalDate.of(displayedMonth.year, displayedMonth.month, it)
                                    selectedDate = newDate
                                    onDateSelected(newDate)
                                }
                            },
                            backgroundColor = if (day == null) Color.Transparent else cardBackgroundColor,
                            strokeColor = if (selectedDate?.dayOfMonth == day) selectedDayStrokeColor else cardStrokeColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayBox(
    day: Int?,
    isSelected: Boolean,
    score: Int?,
    onClick: () -> Unit,
    backgroundColor: Color,
    strokeColor: Color
) {
    val filledColor = when {
        score == null -> backgroundColor
        score <= 33 -> Color(0xFFFF7043) // Soft red-orange tones for lower scores
        score <= 66 -> Color(0xFFFFA726) // Soft yellow-orange tones for medium scores
        else -> Color(0xFF8BC34A) // Olive green tones for higher scores
    }

    // Hedef dolgu yüksekliğini hesapla
    val targetFillHeight = (score ?: 0) * 0.53.dp

    // Animasyonlu dolgu yüksekliği
    val animatedFillHeight by animateDpAsState(
        targetValue = targetFillHeight,
        animationSpec = tween(durationMillis = 1250)
    )

    Box(
        modifier = Modifier
            .width(52.dp).height(56.dp)
            .background(
                color = filledColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(2.dp, strokeColor, RoundedCornerShape(10.dp))
            .clickable(enabled = day != null) { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        if (day != null) {
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(animatedFillHeight) // Animasyonlu yüksekliği kullan
                    .background(filledColor, shape = RoundedCornerShape(10.dp))
            )
            Text(
                text = day.toString(),
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val sampleMoodData = mapOf(
        LocalDate.of(2024, 10, 5) to 20, // Score of 20
        LocalDate.of(2024, 10, 6) to 47, // Score of 47
        LocalDate.of(2024, 10, 7) to 35, // Score of 35
        LocalDate.of(2024, 10, 10) to 80, // Score of 80
        LocalDate.of(2024, 10, 11) to 65, // Score of 65
        LocalDate.of(2024, 10, 12) to 53, // Score of 53
        LocalDate.of(2024, 10, 14) to 92, // Score of 92
        LocalDate.of(2024, 10, 15) to 78, // Score of 78
        LocalDate.of(2024, 10, 17) to 34, // Score of 34
        LocalDate.of(2024, 10, 20) to 49, // Score of 49
        LocalDate.of(2024, 10, 21) to 68, // Score of 68
        LocalDate.of(2024, 10, 22) to 33, // Score of 15
        LocalDate.of(2024, 10, 23) to 88, // Score of 88
        LocalDate.of(2024, 10, 24) to 77, // Score of 77
        LocalDate.of(2024, 10, 25) to 55, // Score of 55
        LocalDate.of(2024, 10, 26) to 23, // Score of 23
        LocalDate.of(2024, 10, 27) to 94, // Score of 94
        LocalDate.of(2024, 10, 28) to 62, // Score of 62
        LocalDate.of(2024, 10, 29) to 100, // Score of 39
        LocalDate.of(2024, 10, 30) to 1  // Score of 58
    )

    MaterialTheme {
        GradientBackground()
        Calendar(
            currentMonth = YearMonth.now(),
            initialSelectedDate = LocalDate.now(),  // selectedDate yerine initialSelectedDate
            moodData = sampleMoodData,
            onDateSelected = {},
            onMonthChanged = {}
        )
    }
}
