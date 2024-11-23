package com.example.depresentry.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.depresentry.R
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.ConfirmDialog
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.SettingsButton
import com.example.depresentry.presentation.navigation.AuthScreen
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val fullName by viewModel.fullName
    val email by viewModel.email
    val localProfileImagePath by viewModel.localProfileImagePath
    val logoutSuccess by viewModel.logoutSuccess

    var switchStates = remember { mutableStateMapOf<String, Boolean>() }
    var showDialogFor by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            navController.navigate(AuthScreen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    GradientBackground()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color.Transparent,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 8.dp)
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
                Spacer(modifier = Modifier.height(32.dp))
                // Profile Image
                if (localProfileImagePath != null) {
                    AsyncImage(
                        model = localProfileImagePath,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Information
                Text(
                    text = fullName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE3CCF2),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color(0xFFF9F775)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Edit Profile Button
                DSBasicButton(
                    onClick = { navController.navigate(MainScreen.EditProfile.route) },
                    buttonText = "Edit Profile",
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(32.dp))

                // App Preferences Section
                Text(
                    text = "App Preferences",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF9F775)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )
            }

            items(listOf("Language", "Notification Settings", "Theme")) { setting ->
                SettingsButton(text = setting) { /* Setting action */ }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Privacy & Permissions Section
                Text(
                    text = "Privacy & Permissions",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF9F775)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )
            }

            // List of Privacy & Permissions Settings with Toggle Switches
            items(
                listOf(
                    "Sleep Tracking",
                    "Pedometer",
                    "App Usage Data",
                    "Location",
                    "Contacts",
                    "Camera Access"
                )
            ) { setting ->
                val isChecked = switchStates[setting] ?: false
                SettingsButton(
                    text = setting,
                    switch = true,
                    checked = isChecked,
                    onSwitchChange = {
                        showDialogFor = setting
                    },
                    onClick = {
                        showDialogFor = setting
                    }
                )
            }

            item{
                Text(
                    text = "Log Out",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF9F775),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp)
                        .clickable {
                            viewModel.logout()
                        }
                )
            }
        }

        // Confirmation Dialog
        showDialogFor?.let { setting ->
            ConfirmDialog(
                text = "Are you sure you want to change $setting?",
                onConfirm = {
                    switchStates[setting] = !(switchStates[setting] ?: false)
                    showDialogFor = null
                },
                onDismiss = { showDialogFor = null }
            )
        }
    }
}
