package com.adkins.msafari.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    showTripStatus: Boolean // 👈 New conditional flag
) {
    NavigationBar(
        containerColor = Black,
        contentColor = White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentRoute == Screen.ClientHome.route,
            onClick = { onNavigate(Screen.ClientHome.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green,
                unselectedIconColor = White
            )
        )

        // ✅ Conditionally show Trip Status
        if (showTripStatus) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Place, contentDescription = "Trip Status") },
                selected = currentRoute == Screen.ClientTripStatus.route,
                onClick = { onNavigate(Screen.ClientTripStatus.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Green,
                    unselectedIconColor = White
                )
            )
        }

        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            selected = currentRoute == Screen.BookingHistory.route,
            onClick = { onNavigate(Screen.BookingHistory.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green,
                unselectedIconColor = White
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green,
                unselectedIconColor = White
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = currentRoute == Screen.Profile.route,
            onClick = { onNavigate(Screen.Profile.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green,
                unselectedIconColor = White
            )
        )
    }
}