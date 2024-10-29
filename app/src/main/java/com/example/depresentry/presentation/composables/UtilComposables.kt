package com.example.depresentry.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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



@Preview(showBackground = true)
@Composable
fun UtilPreview() {

}
