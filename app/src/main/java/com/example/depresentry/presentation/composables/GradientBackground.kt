package com.example.depresentry.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun GradientBackground() {
    // Background color (#130B18)
    val backgroundColor = Color(0xFF130B18)


    val gradientBrush = Brush.radialGradient(
        colors = listOf(Color(0xeeD2B7E4).copy(alpha = 0.5f), Color.Transparent),
        center = androidx.compose.ui.geometry.Offset(1925f, 1200f),
        radius = 1800f // Adjust radius for the circular effect
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .graphicsLayer {
                renderEffect = BlurEffect(
                    radiusX = 20.dp.toPx(),
                    radiusY = 20.dp.toPx()
                )
            }
            .background(gradientBrush)
    )

}

@Preview
@Composable
fun PreviewGradientBackground() {
    GradientBackground()
}