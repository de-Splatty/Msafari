package com.adkins.msafari.ui.theme.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.ClientScaffoldWrapper
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.utils.NotificationUtils
import com.adkins.msafari.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSettingsScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val darkModeEnabled by settingsViewModel.darkModeEnabled.collectAsState()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Msafari") },
            text = {
                Text("Msafari is a tour booking app connecting travelers to drivers across the country.\n\nVersion 1.0.0")
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    ClientScaffoldWrapper(
        title = "Settings",
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        showTripStatus = true,
        showProfile = true, // ✅ Added required parameter
        content = { innerPadding -> // ✅ Fixed content lambda
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

                Text("Security", color = colors.primary, style = MaterialTheme.typography.titleMedium)

                SettingItem(
                    icon = Icons.Default.LockReset,
                    label = "Change Password",
                    colors = colors,
                    onClick = {
                        val user = AuthManager.currentUser
                        val email = user?.email
                        if (email != null) {
                            AuthManager.sendPasswordResetEmail(
                                email = email,
                                onSuccess = {
                                    Toast.makeText(context, "Reset email sent to $email", Toast.LENGTH_LONG).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                SettingItem(
                    icon = Icons.Default.Logout,
                    label = "Logout",
                    colors = colors,
                    onClick = {
                        AuthManager.logout()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        onLogoutSuccess()
                    }
                )

                Divider(color = colors.primary)

                Text("General", color = colors.primary, style = MaterialTheme.typography.titleMedium)

                SettingItem(
                    icon = Icons.Default.PrivacyTip,
                    label = "Privacy Policy",
                    colors = colors
                )

                SettingItem(
                    icon = Icons.Default.Info,
                    label = "Help & Support",
                    colors = colors
                )

                SettingItem(
                    icon = Icons.Default.Star,
                    label = "Rate the App",
                    colors = colors,
                    onClick = {
                        val uri = Uri.parse("market://details?id=" + context.packageName)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Play Store not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                SettingItem(
                    icon = Icons.Default.Info,
                    label = "About Msafari",
                    colors = colors,
                    onClick = { showAboutDialog = true }
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    "App Version 1.0.0",
                    color = colors.onBackground,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}

@Composable
fun SettingToggle(
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    colors: ColorScheme
) {
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
fun SettingItem(
    icon: ImageVector,
    label: String,
    colors: ColorScheme,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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