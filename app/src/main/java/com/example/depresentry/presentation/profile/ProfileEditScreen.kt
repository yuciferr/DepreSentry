package com.example.depresentry.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DSDropDown
import com.example.depresentry.presentation.composables.DSTextField
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont


@Composable
fun ProfileEditScreen(navController: NavController) {

    val viewModel: ProfileEditViewModel = hiltViewModel()

    // State values from ViewModel
    val fullName by viewModel.fullName
    val age by viewModel.age
    val profession by viewModel.profession
    val gender by viewModel.gender
    val maritalStatus by viewModel.maritalStatus
    val country by viewModel.country
    val isLoading by viewModel.isLoading
    val updateError by viewModel.updateError
    val updateSuccess by viewModel.updateSuccess

    // Load user profile when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            navController.navigate(MainScreen.Home.route) {
                popUpTo(MainScreen.EditProfile.route) { inclusive = true }
            }
        }
    }
    var showSnackbar by remember { mutableStateOf(false) }

    val fullNameFocusRequester = FocusRequester()
    val ageFocusRequester = FocusRequester()
    val professionFocusRequester = FocusRequester()

    GradientBackground()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DepreSentry",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = logoFont,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(56.dp))

        Column(
            modifier = Modifier
                .clickable { /* TODO: Handle profile picture click */ }
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFFD4D4D4),
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Add a profile photo",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFFE3CCF2)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DSTextField(
            label = "Full Name",
            value = viewModel.fullName.value,
            placeholder = "Enter your full name",
            onValueChange = { viewModel.fullName.value = it },
            imeAction = ImeAction.Next,
            onImeAction = { fullNameFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(fullNameFocusRequester)
        )

        DSDropDown(
            label = "Gender",
            value = gender,
            items = listOf("Male", "Female", "Other"),
            onValueChange = { viewModel.gender.value = it }
        )

        DSTextField(
            label = "Age",
            value = age,
            placeholder = "Enter your age",
            keyboardType = KeyboardType.Number,
            onValueChange = { viewModel.age.value = it },
            imeAction = ImeAction.Next,
            onImeAction = { ageFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(fullNameFocusRequester)
        )

        DSTextField(
            label = "Profession",
            value = profession,
            placeholder = "Enter your profession",
            onValueChange = { viewModel.profession.value = it },
            imeAction = ImeAction.Next,
            onImeAction = { professionFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(fullNameFocusRequester)
        )

        DSDropDown(
            label = "Marital Status",
            value = maritalStatus,
            items = listOf("Single", "Married", "Divorced", "Widowed"),
            onValueChange = { viewModel.maritalStatus.value = it }
        )

        DSDropDown(
            label = "Country",
            value = country,
            items = listOf(
                "USA",
                "Canada",
                "UK",
                "Germany",
                "France"
            ), // Add more countries as needed
            onValueChange = { viewModel.country.value = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .padding(16.dp)
            )
        }


        DSBasicButton(
            onClick = {
                viewModel.updateUserProfile()
                if (updateError != null) {
                    showSnackbar = true
                }
            },
            buttonText = "Save"
        )

        // Show Snackbar for errors
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                content = {
                    Text(
                        text = updateError ?: "An error occurred",
                        color = Color.White
                    )
                },
                containerColor = Color.Red
            )
        }
    }
}

@Preview
@Composable
fun ProfileEditScreenPreview() {
    //ProfileEditScreen()
}

