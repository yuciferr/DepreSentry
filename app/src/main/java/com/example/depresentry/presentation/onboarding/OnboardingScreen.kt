package com.example.depresentry.presentation.onboarding

import OnBoardingCard
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.depresentry.R
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.AuthScreen


@Composable
fun OnboardingScreen(
    navController: NavController
) {
    // State to track the current onboarding step
    var currentStep by remember { mutableIntStateOf(0) }

    // Content for each onboarding step
    val onboardingContent = listOf(
        Pair(
            R.drawable.onb1, "Mood Tracking Made\n" +
                    "Simple"
        ),
        Pair(
            R.drawable.onb2, "Monitor Your Activity\n" +
                    "& Sleep"
        ),
        Pair(R.drawable.onb3, "Stay Ahead with Early Warnings")
    )

    // Details for each step in the OnBoardingCard
    val onboardingDetails = listOf(
        "Easily track your daily mood and emotional well-being with just a few taps. Get personalized insights to understand your mental health better.",
        "Stay informed about your physical activity, sleep patterns, and how they influence your mood. All your data in one place for a complete overview.",
        "Receive early notifications and tailored advice to help prevent depressive episodes. Take control of your mental health with proactive alerts."
    )

    // Animation content between different onboarding steps
    GradientBackground()
    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            // Slide animation
            if (targetState > initialState) {
                slideInHorizontally(animationSpec = tween(500)) { width -> width } togetherWith
                        slideOutHorizontally(animationSpec = tween(500)) { width -> -width }
            } else {
                slideInHorizontally(animationSpec = tween(500)) { width -> -width } togetherWith
                        slideOutHorizontally(animationSpec = tween(500)) { width -> width }
            }
        }, label = ""
    ) { targetStep ->
        Image(
            painter = painterResource(id = onboardingContent[targetStep].first),
            contentDescription = "Onboarding Image",
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            contentScale = ContentScale.FillBounds
        )
    }
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        if (currentStep < onboardingContent.size - 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Skip",
                    color = Color(0xF0F9F775),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(AuthScreen.SignUp.route) {
                                popUpTo(AuthScreen.Onboarding.route) { inclusive = true }
                            }
                        }
                        .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.small)
                )

            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Onboarding Card
        OnBoardingCard(
            title = onboardingContent[currentStep].second,
            detail = onboardingDetails[currentStep]
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Next/Get Started Button
        DSBasicButton(
            onClick = {
                if (currentStep < onboardingContent.size - 1) {
                    // Go to the next step
                    currentStep++
                } else {
                    navController.navigate(AuthScreen.SignUp.route) {
                        popUpTo(AuthScreen.Onboarding.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            buttonText = if (currentStep < onboardingContent.size - 1) "Next" else "Get Started"
        )
    }

}

@Preview
@Composable
fun OnboardingScreenPreview() {
    //OnboardingScreen()
}
