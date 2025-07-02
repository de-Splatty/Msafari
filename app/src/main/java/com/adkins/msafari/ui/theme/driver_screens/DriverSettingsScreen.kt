package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun DriverSettingsScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    DriverScaffoldWrapper(
        title = "Driver Settings",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            Text(
                text = "Settings",
                color = Green,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingItem(label = "Notifications", value = "Enabled")
            SettingItem(label = "Language", value = "English")
            SettingItem(label = "Theme", value = "Dark Mode")

            Spacer(modifier = Modifier.weight(1f))

            DriverActionButton(
                text = "Back to Dashboard",
                icon = Icons.Default.ArrowBack,
                onClick = { onNavigate(Screen.DriverDashboard.route) }
            )

            DriverActionButton(
                text = "Logout",
                icon = Icons.Default.ExitToApp,
                onClick = onLogout
            )
        }
    }
}

@Composable
fun SettingItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = White.copy(alpha = 0.6f), fontSize = 14.sp)
        Text(text = value, color = White, fontSize = 16.sp)
    }
}

@Composable
fun DriverActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Green),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Black)
    }
}