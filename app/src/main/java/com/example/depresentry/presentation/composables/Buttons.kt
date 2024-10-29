package com.example.depresentry.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depresentry.R


@Composable
fun DSBasicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String,
    icon: ImageVector? = null // Parameter for the icon
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD6BBFB),
            contentColor = Color(0xFF3B255A)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Center content horizontally
        ) {
            if (icon != null) { // Only show icon if provided
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Text(
                text = buttonText,
                maxLines = 1,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally) // Center text within its space
            )
        }
    }
}




@Composable
fun SurveyButton(text: String, painter: Painter? = null, onClick: () -> Unit = {}) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor), // Stroke
        shape = RoundedCornerShape(10.dp), // Same corner radius
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Image aligned to the left
            if (painter != null) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart) // Align image to the start (left)
                        .padding(end = 16.dp)
                        .size(32.dp)
                )
            }

            // Text centered
            Text(
                text = text,
                fontFamily = FontFamily.Default, // Roboto by default
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth() // Make text fill the width
                    .align(Alignment.Center) // Center the text vertically
            )
        }

    }
}

@Preview
@Composable
fun PreviewBlurredCardWithText() {
    SurveyButton(
        text = "Not at all",
        painter = if (LocalInspectionMode.current) {
            null // Or a placeholder drawable: painterResource(id = R.drawable.placeholder_icon)
        } else {
            painterResource(id = R.drawable.emoji_excellent) // Your actual icon resource
        },

    ){}
}
