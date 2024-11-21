package com.example.depresentry.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.depresentry.R
import com.example.depresentry.presentation.composables.ActivityCard
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.DailyCard
import com.example.depresentry.presentation.composables.ExpandableFab
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.StatsCard
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun HomeScreen(navController: NavHostController) {
    var isFabExpanded by remember { mutableStateOf(false) }

    GradientBackground()
    Scaffold(
        floatingActionButton = {
            ExpandableFab(
                onAddPHQ9Click = {
                    navController.navigate(MainScreen.PHQ9.route)
                },
                onEnterMoodClick = {
                    navController.navigate(MainScreen.MoodEntry.route)
                },
                expanded = isFabExpanded,
                onFabToggle = { isFabExpanded = !isFabExpanded }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.Transparent,

        ) { contentPadding ->

        // Glassmorphism-style blurred background layer
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f // Opacity for a semi-transparent effect
                        shape = RoundedCornerShape(16.dp) // Rounded corners for a polished look
                        clip = true
                    }
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 1f),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isFabExpanded) 0.5f else 1f)
                .blur(if (isFabExpanded) 20.dp else 0.dp)
                .padding(contentPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "DepreSentry",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE3CCF2),
                            fontFamily = logoFont,
                        ),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Good Morning,",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "User Name",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Thin,
                                color = Color.White,
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                navController.navigate(MainScreen.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                    )
                }
            }

            item {
                DailyCard(
                    score = 84,
                    message = "Your mental health score is improving, you're on the right track!",
                    message2 = "Consider reaching out to a friend for a quick chat or coffee.",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }

            item {
                StatsCard(
                    title = "Mood",
                    stats = listOf("Excellent" to 157),
                    weeklyData = listOf(2, 4, 3, 5, 3, 4, 1),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    color = Color(0xFFCBC4CF)
                ) {
                    MainScreen.DetailedStats.title = "Mood"
                    navController.navigate(MainScreen.DetailedStats.route)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActivityCard(
                        title = "Steps",
                        value = 3124,
                        unit = "Steps",
                        color = Color(0xFFCB6589),
                        targetValue = 10000,
                        modifier = Modifier.weight(1f)
                    ) {
                        MainScreen.DetailedStats.title = "Steps"
                        navController.navigate(MainScreen.DetailedStats.route)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ActivityCard(
                        title = "Sleep",
                        value = 6,
                        unit = "Hours",
                        color = Color(0xFFE2E06A),
                        targetValue = 8,
                        modifier = Modifier.weight(1f)
                    ) {
                        MainScreen.DetailedStats.title = "Sleep"
                        navController.navigate(MainScreen.DetailedStats.route)
                    }
                }
            }

            item {
                StatsCard(
                    title = "Screen Time",
                    stats = listOf("WhatsApp" to 157, "Youtube" to 72),
                    weeklyData = listOf(2, 3, 4, 2, 3, 4, 1),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    color = Color(0xFFC136A8)
                ) {
                    MainScreen.DetailedStats.title = "Screen Time"
                    navController.navigate(MainScreen.DetailedStats.route)
                }
            }
        }

    }
}
