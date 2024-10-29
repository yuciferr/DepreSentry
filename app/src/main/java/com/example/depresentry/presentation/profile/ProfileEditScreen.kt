package com.example.depresentry.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DSTextField
import com.example.depresentry.presentation.composables.GradientBackground
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.depresentry.presentation.composables.DSDropDown
import com.example.depresentry.presentation.theme.logoFont


@Composable
fun ProfileEditScreen() {
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

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
            value = fullName,
            placeholder = "Enter your full name",
            onValueChange = { fullName = it }
        )

        DSDropDown(
            label = "Gender",
            value = gender,
            items = listOf("Male", "Female", "Other"),
            onValueChange = { gender = it }
        )

        DSTextField(
            label = "Age",
            value = age,
            placeholder = "Enter your age",
            keyboardType = KeyboardType.Number,
            onValueChange = { age = it }
        )

        DSTextField(
            label = "Profession",
            value = profession,
            placeholder = "Enter your profession",
            onValueChange = { profession = it }
        )

        DSDropDown(
            label = "Marital Status",
            value = maritalStatus,
            items = listOf("Single", "Married", "Divorced", "Widowed"),
            onValueChange = { maritalStatus = it }
        )

        DSDropDown(
            label = "Country",
            value = country,
            items = listOf("USA", "Canada", "UK", "Germany", "France"), // Add more countries as needed
            onValueChange = { country = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        DSBasicButton(
            onClick = { /* TODO: Handle save action */ },
            buttonText = "Save"
        )
    }
}

@Preview
@Composable
fun ProfileEditScreenPreview() {
    ProfileEditScreen()
}

