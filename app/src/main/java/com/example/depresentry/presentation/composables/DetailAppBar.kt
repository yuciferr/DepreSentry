package com.example.depresentry.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailAppBar(title: String, detail: String, onBackClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(top= 32.dp, bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color(0xFFE3CCF2), // Başlık rengi
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

            }
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color(0xFFF9F775),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Text(
            text = detail,
            color = Color(0xFFF9F775), // Detay rengi
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun DetailAppBarPreview() {
    GradientBackground()
    DetailAppBar(title = "PHQ-9 Depression", detail = "2 to 9", onBackClick = {})
}
