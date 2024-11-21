package com.example.depresentry.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    modifier: Modifier = Modifier.fillMaxWidth(),
    buttonText: String,
    icon: ImageVector? = null // Parameter for the icon
) {
    Button(
        onClick = onClick,
        modifier = modifier,
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

@Composable
fun SettingsButton(
    text: String,
    switch: Boolean = false,
    checked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val cardBackgroundColor = Color(0x23E8D1F7)
    val cardStrokeColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardStrokeColor), // Stroke
        shape = RoundedCornerShape(10.dp), // Same corner radius
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp)
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.Default, // Roboto by default
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = textColor,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
            )
            if (switch) {
                DSSwitch(
                    checked = checked,
                    onCheckedChange = onSwitchChange,
                    scale = 1.15f
                )
            }
        }

    }
}

@Composable
fun DSSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1.2f  // Varsayılan ölçek faktörü
) {
    val checkedThumbColor = Color(0xFF3B255A)
    val checkedTrackColor = Color(0xFFD6BBFB)
    val uncheckedThumbColor = Color(0xFF958E99)
    val uncheckedTrackColor = Color(0xFF37333A)

    Box(
        modifier = modifier
            .scale(scale)  // Scale transform uygula
            .padding(horizontal = (4 * scale).dp)  // Ölçeğe göre padding ekle
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Default.Done else Icons.Default.Close,
                    contentDescription = if (checked) "Checked" else "Unchecked",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)  // Icon boyutu
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = checkedThumbColor,
                checkedTrackColor = checkedTrackColor,
                uncheckedThumbColor = uncheckedThumbColor,
                uncheckedTrackColor = uncheckedTrackColor
            )
        )
    }
}

@Composable
fun ExpandableFab(
    onAddPHQ9Click: () -> Unit,
    onEnterMoodClick: () -> Unit,
    expanded: Boolean,
    onFabToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Genişleyen küçük FAB'ler
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                MiniFab(
                    text = "Add PHQ-9",
                    onClick = onAddPHQ9Click,
                    iconResId = R.drawable.ic_launcher_foreground
                )
                Spacer(modifier = Modifier.height(8.dp))
                MiniFab(
                    text = "Enter Mood",
                    onClick = onEnterMoodClick,
                    iconResId = R.drawable.ic_launcher_foreground
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Ana FAB
        FloatingActionButton(
            onClick = onFabToggle,
            containerColor = Color(0xFFD5BAF9)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Toggle",
                tint = Color(0xFF3B255A)
            )
        }
    }
}

@Composable
fun MiniFab(
    text: String,
    onClick: () -> Unit,
    iconResId: Int?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color(0xFFD5BAF9)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = text,
                tint = Color(0xFF3B255A)
            )
        }
    }
}

@Preview
@Composable
fun PreviewBlurredCardWithText() {
    SettingsButton(
        text = "Sleep Tracking",
        switch = false,
        checked = true,
    ) {}
}
