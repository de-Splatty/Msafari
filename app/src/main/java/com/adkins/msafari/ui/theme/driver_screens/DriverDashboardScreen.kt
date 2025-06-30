package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun DriverDashboardScreen(
    onNavigateToPastDrives: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Driver Dashboard",
            color = Green,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DashboardCard(
            title = "Past Drives",
            icon = Icons.Default.History,
            onClick = onNavigateToPastDrives
        )

        DashboardCard(
            title = "Payments",
            icon = Icons.Default.AttachMoney,
            onClick = onNavigateToPayments
        )

        DashboardCard(
            title = "Settings",
            icon = Icons.Default.Settings,
            onClick = onNavigateToSettings
        )

        DashboardCard(
            title = "Alerts",
            icon = Icons.Default.Notifications,
            onClick = onNavigateToAlerts
        )

        DashboardCard(
            title = "Profile",
            icon = Icons.Default.AccountCircle,
            onClick = onNavigateToProfile
        )

        DashboardCard(
            title = "Logout",
            icon = Icons.Default.ExitToApp,
            onClick = onLogout
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Stay safe, and enjoy the ride!",
            color = White,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Green)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = White, fontSize = 18.sp)
        }
    }
}