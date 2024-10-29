package com.example.depresentry.presentation.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DSTextField
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.OrDivider
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        Spacer(modifier = Modifier.height(72.dp))

        Text(
            text = "Welcome back 👋",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F775),
                shadow = Shadow(color = Color.Black, blurRadius = 4f),
            ),
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Log in to your account to continue.",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color(0xFFE3CCF2),
                shadow = Shadow(color = Color.Black, blurRadius = 4f),
            ),
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))


        DSTextField(
            label = "Email",
            value = email,
            placeholder = "Enter your email",
            onValueChange = { newValue -> email = newValue }
        )

        // Password Input Field
        DSTextField(
            label = "Password",
            value = password,
            isPassword = true,
            placeholder = "Enter your password",
            onValueChange = { newValue -> password = newValue }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Forgot Password
        Text(
            text = "Forgot Password?",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFFE3CCF2)
            ),
            modifier = Modifier.align(Alignment.End).clickable {
                // TODO: Handle Forgot Password click
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Log In Button
        DSBasicButton(
            onClick = { /* TODO: Handle log in */ },
            buttonText = "Log In"
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign In Button
        DSBasicButton(
            onClick = { /* TODO: Handle Google sign in */ },
            buttonText = "Log in with Google",
            icon = Icons.Default.AccountCircle
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sign Up Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Don’t have an account?",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFFE3CCF2)
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign Up",
                color = Color(0xFFF9F775),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    // TODO: Handle Sign Up click
                }
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
