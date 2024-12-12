package com.example.depresentry.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeRangeSelector(
    selectedRange: String,
    onRangeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0x1AFFFFFF),
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Daily", "Weekly", "Monthly").forEach { range ->
            Text(
                text = range,
                color = if (selectedRange == range) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontWeight = if (selectedRange == range) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clickable { onRangeSelected(range) }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            )
        }
    }
}