package com.adkins.msafari.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adkins.msafari.utils.NotificationUtils
import com.adkins.msafari.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val darkModeEnabled by settingsViewModel.darkModeEnabled.collectAsState()
    val context = LocalContext.current

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = colors.onPrimary, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.primary)
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Preferences", color = colors.primary, style = MaterialTheme.typography.titleMedium)

            SettingToggle(
                title = "Enable Notifications",
                checked = notificationsEnabled,
                onToggle = {
                    settingsViewModel.toggleNotifications(it)
                    if (it) {
                        NotificationUtils.sendTestNotification(
                            context = context,
                            title = "Notifications Enabled",
                            message = "You will now receive updates from Msafari!"
                        )
                    }
                },
                colors = colors
            )

            SettingToggle(
                title = "Dark Mode",
                checked = darkModeEnabled,
                onToggle = { settingsViewModel.toggleDarkMode(it) },
                colors = colors
            )

            Divider(color = colors.primary)

            Text("General", color = colors.primary, style = MaterialTheme.typography.titleMedium)

            SettingItem(icon = Icons.Default.PrivacyTip, label = "Privacy Policy", colors = colors)
            SettingItem(icon = Icons.Default.Info, label = "Help & Support", colors = colors)
            SettingItem(icon = Icons.Default.Star, label = "Rate the App", colors = colors)
            SettingItem(icon = Icons.Default.Info, label = "About Msafari", colors = colors)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "App Version 1.0.0",
                color = colors.onBackground,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingToggle(title: String, checked: Boolean, onToggle: (Boolean) -> Unit, colors: ColorScheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = colors.onBackground)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = colors.onBackground,
                uncheckedTrackColor = colors.onBackground.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingItem(icon: ImageVector, label: String, colors: ColorScheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(label, color = colors.onBackground)
    }
}