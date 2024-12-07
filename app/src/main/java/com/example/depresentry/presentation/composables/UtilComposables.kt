package com.example.depresentry.presentation.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            color = Color(0xFF4A454E),
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f) // Sol Divider'ın genişliğini esnek yap
        )

        Text(
            text = "Or",
            color = Color(0xFFD6BBFB),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp) // Metin ile Divider'lar arasında boşluk
        )

        Divider(
            color = Color(0xFF4A454E),
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f) // Sağ Divider'ın genişliğini esnek yap
        )
    }
}

@Composable
fun AgreementCheckbox(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,

    ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFD6BBFB),
                checkmarkColor = Color(0xFF1E1622)
            )
        )
        Text(
            text = text,
            color = Color(0xFFE3CCF2),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun ConfirmDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val cardBackgroundColor = Color(0xF347404C)
    val textColor = Color(0xFFE3CCF2)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes", color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No", color = textColor)
            }
        },
        text = {
            Text(
                text = text,
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        },
        containerColor = cardBackgroundColor, // Background color of the dialog
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 2.dp,
        properties = DialogProperties()
    )
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    val shimmerColors = listOf(
        Color(0xFF3A3A3A),
        Color(0xFF6E6E6E),
        Color(0xFF3A3A3A)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim.value - 1000f, 0f),
        end = Offset(x = translateAnim.value, 0f)
    )

    Spacer(
        modifier = modifier
            .background(brush)
    )
}


@Preview(showBackground = true)
@Composable
fun UtilPreview() {
    ConfirmDialog(
        text = "Are you sure you want to change Sleep?",
        {},{}
    )
}
