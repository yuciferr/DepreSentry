package com.example.depresentry.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.depresentry.presentation.navigation.MainScreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem(MainScreen.Home, Icons.Filled.Home, "Home"),
        BottomNavItem(MainScreen.Calendar, Icons.Filled.DateRange, "Calendar"),
        BottomNavItem(MainScreen.Profile, Icons.Filled.Person, "Profile")
    )

    val selectedColor = Color(0xFF806691)
    val textColor = Color(0xFFE3CCF2)

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Transparent)
                .blur(50.dp)
        ){
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0x350E0B10)))
        }
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items.forEach { screen ->
                val isSelected = navController.currentDestination?.route == screen.screen.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            screen.icon,
                            contentDescription = screen.label,
                            tint = if (isSelected) selectedColor else textColor,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    },
                    label = {
                        if (isSelected) {
                            Text(
                                screen.label,
                                color = textColor,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(screen.screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val screen: MainScreen,
    val icon: ImageVector,
    val label: String
)

@Preview
@Composable
fun BottomNavigationBarPreview() {
    GradientBackground()
    Column(modifier = Modifier.fillMaxSize()){
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(navController = NavHostController(LocalContext.current))
    }
}
