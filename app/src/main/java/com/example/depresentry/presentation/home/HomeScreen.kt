package com.example.depresentry.presentation.home

import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.depresentry.R
import com.example.depresentry.presentation.composables.ActivityCard
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.DailyCard
import com.example.depresentry.presentation.composables.ExpandableFab
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.ShimmerEffect
import com.example.depresentry.presentation.composables.StatsCard
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var isFabExpanded by remember { mutableStateOf(false) }
    val fullName by viewModel.fullName
    val localProfileImagePath by viewModel.localProfileImagePath
    val isLoading by viewModel.isLoading

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
                onEnterSleepClick = {
                    navController.navigate(MainScreen.SleepEntry.route)
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
                        if (isLoading) {
                            ShimmerEffect(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ShimmerEffect(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        } else {
                            Text(
                                text = getGreetingMessage(),
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                ),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Text(
                                text = fullName,
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Thin,
                                    color = Color.White,
                                ),
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                    
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        ) {
                            ShimmerEffect(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        if (localProfileImagePath != null) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        navController.navigate(MainScreen.Profile.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                            ) {
                                AsyncImage(
                                    model = localProfileImagePath,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        navController.navigate(MainScreen.Profile.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.avatar),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            item {
                if (isLoading) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    DailyCard(
                        score = viewModel.depressionScore.value.toInt(),
                        message = viewModel.welcomeMessage.value,
                        message2 = viewModel.affirmationMessage.value,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }

            item {
                if (isLoading) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
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
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (isLoading) {
                        ShimmerEffect(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ShimmerEffect(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    } else {
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
            }

            item {
                if (isLoading) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    if (!viewModel.hasUsageStatsPermission.value) {
                        // İzin yoksa basit bir kart göster
                        StatsCard(
                            title = "Screen Time",
                            stats = listOf("Permission Required" to 0),
                            weeklyData = listOf(0, 0, 0, 0, 0, 0, 0),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                            color = Color(0xFFC136A8)
                        ) {
                            MainScreen.DetailedStats.title = "Screen Time"
                            navController.navigate(MainScreen.DetailedStats.route)
                        }
                    } else {
                        // İzin varsa gerçek verileri göster
                        val screenTimeData = viewModel.screenTimeStats.value
                        val topApps = screenTimeData.entries
                            .sortedByDescending { it.value }
                            .take(2)
                            .map { (name, duration) -> 
                                name to (duration / (1000 * 60)).toInt() // Dakikaya çevir
                            }

                        StatsCard(
                            title = "Screen Time",
                            stats = topApps,
                            weeklyData = listOf(2, 3, 4, 2, 3, 4, 1), // Bu kısmı gerçek verilerle değiştirebilirsiniz
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

    }
}

private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..5 -> "Good Night,"
        in 6..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        in 17..20 -> "Good Evening,"
        else -> "Good Night,"
    }
}
