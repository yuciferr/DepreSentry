import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

@Composable
fun OnBoardingCard(title: String, detail: String) {
    // Alt CardView için renkler ve stil
    val cardBackgroundColor = Color(0x239574AA) // 20% opacity of #9574AA
    val cardStrokeColor = Color(0xFF806691) // Stroke color
    val titleColor = Color(0xF0F9F775) // 94% opacity of #F9F775
    val detailColor = Color(0xCCF9F775) // 80% opacity of #F9F775

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
    ) {
        // Alt CardView (Blurred ve stroke'lu, içeriksiz)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f) // En-boy oranı ile sabit boyutta tutuyoruz
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
            shape = RoundedCornerShape(10.dp), // Corner radius
        ) {
        }

        // Üst CardView (Şeffaf, arka plan ve stroke yok, sadece metin var)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize() // Alt Card ile aynı boyutta olacak
                .align(Alignment.Center), // Box içinde ortalıyoruz
            colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Transparent background
            border = BorderStroke(1.dp, cardStrokeColor), // Stroke
            shape = RoundedCornerShape(10.dp), // Same corner radius
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title text
                Text(
                    text = title,
                    fontFamily = FontFamily.Default, // Roboto by default
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = titleColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Detail text
                Text(
                    text = detail,
                    fontFamily = FontFamily.Default, // Roboto by default
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    color = detailColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBlurredCardWithText() {
    OnBoardingCard(
        title = "Stay Ahead with Early Warnings",
        detail = "Receive early notifications and tailored advice to help prevent depressive episodes. Take control of your mental health with proactive alerts."
    )
}
