package com.example.depresentry.presentation.auth.signin

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
import com.example.depresentry.presentation.composables.AgreementCheckbox
import com.example.depresentry.presentation.composables.DSBasicButton
import com.example.depresentry.presentation.composables.DSTextField
import com.example.depresentry.presentation.composables.GradientBackground
import com.example.depresentry.presentation.composables.OrDivider
import com.example.depresentry.presentation.navigation.AuthScreen
import com.example.depresentry.presentation.navigation.MainScreen
import com.example.depresentry.presentation.theme.logoFont

@Composable
fun SignUpScreen(
    navController: NavController
) {
    val viewModel: SignUpScreenViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var showPolicyWarning by remember { mutableStateOf(false) } // For policy warning

    val isLoading by viewModel.isLoading
    val signUpError by viewModel.signUpError
    val signUpSuccess by viewModel.signUpSuccess

    val fullNameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Navigation effect for sign-up success
    LaunchedEffect(signUpSuccess) {
        if (signUpSuccess) {
            navController.navigate(MainScreen.EditProfile.route) {
                popUpTo(AuthScreen.SignUp.route) { inclusive = true }
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
            text = "Create Your Account",
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
            text = "Join us and start tracking your mental \nhealth today.",
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
            label = "Full Name",
            value = fullName,
            placeholder = "Enter your full name",
            onValueChange = { fullName = it },
            imeAction = ImeAction.Next,
            onImeAction = { fullNameFocusRequester.requestFocus() },
            modifier = Modifier.focusRequester(fullNameFocusRequester)
        )

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
            onImeAction = {
                if (isChecked) {
                    viewModel.registerAndCreateProfile(email, password, fullName)
                } else {
                    showPolicyWarning = true
                }
            },
            modifier = Modifier.focusRequester(passwordFocusRequester)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AgreementCheckbox(
            checked = isChecked,
            text = "By signing up, you agree to our Terms of Service and Privacy Policy."
        ) { isChecked = it }

        if (showPolicyWarning && !isChecked) {
            Text(
                text = "Please agree to the Terms of Service and Privacy Policy.",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button or Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            DSBasicButton(
                onClick = {
                    if (isChecked) {
                        viewModel.registerAndCreateProfile(email, password, fullName)

                    } else {
                        showPolicyWarning = true
                    }
                },
                buttonText = "Sign Up"
            )
        }

        signUpError?.let {
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
            onClick = { /* TODO: Handle Google sign up */ },
            buttonText = "Sign up with Google",
            icon = Icons.Default.AccountCircle
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Already have an account?",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFFE3CCF2)
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Log In",
                color = Color(0xFFF9F775),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(AuthScreen.Login.route)
                }
            )
        }
    }
}
