package com.adkins.msafari.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import com.adkins.msafari.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverScaffoldWrapper(
    title: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    showProfile: Boolean = true,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            DriverBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        content = content
    )
}

@Composable
fun DriverBottomBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        driverNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

private data class DriverNavItem(val label: String, val icon: ImageVector, val route: String)

private val driverNavItems = listOf(
    DriverNavItem("Home", Icons.Default.Dashboard, Screen.DriverDashboard.route),
    DriverNavItem("Payments", Icons.Default.AttachMoney, Screen.DriverPayments.route),
    DriverNavItem("Past Drives", Icons.Default.History, Screen.PastDrives.route),
    DriverNavItem("Profile", Icons.Default.Person, Screen.DriverProfile.route),
    DriverNavItem("Settings", Icons.Default.Settings, Screen.DriverSettings.route)
)