package com.example.depresentry.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.depresentry.presentation.composables.BottomNavigationBar
import com.example.depresentry.presentation.composables.GradientBackground

@Composable
fun HomeScreen(navController: NavHostController) {
    GradientBackground()
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier= Modifier.weight(1f))
        BottomNavigationBar(navController = navController)
    }
}
