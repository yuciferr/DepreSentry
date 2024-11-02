package com.example.depresentry.presentation.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DSTextField
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.OrDivider
import com.example.depresentry.presentation.navigation.AuthScreen
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun LoginScreen(
    navController: NavController,
) {
    val viewModel: LoginScreenViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val loginError by viewModel.loginError
    val loginSuccess by viewModel.loginSuccess

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Navigation effect for sign-up success
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            navController.navigate(MainScreen.Home.route) {
                popUpTo(AuthScreen.Login.route) { inclusive = true }
            }
        }
    }

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
            onValueChange = { email = it },
            imeAction = ImeAction.Next,
            onImeAction = { emailFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(emailFocusRequester)
        )

        DSTextField(
            label = "Password",
            value = password,
            isPassword = true,
            placeholder = "Enter your password",
            onValueChange = { password = it },
            imeAction = ImeAction.Done,
            onImeAction = { viewModel.loginUser(email, password) },
            modifier = Modifier.focusRequester(passwordFocusRequester)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot Password?",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFFE3CCF2)
            ),
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    // TODO: Handle Forgot Password click
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Log In Button or Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            DSBasicButton(
                onClick = {
                    viewModel.loginUser(email, password)
                },
                buttonText = "Log In"
            )
        }

        loginError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OrDivider()

        Spacer(modifier = Modifier.height(16.dp))

        DSBasicButton(
            onClick = { /* TODO: Handle Google sign in */ },
            buttonText = "Log in with Google",
            icon = Icons.Default.AccountCircle
        )

        Spacer(modifier = Modifier.weight(1f))

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
                    navController.navigate(AuthScreen.SignUp.route)
                }
            )
        }
    }
}
