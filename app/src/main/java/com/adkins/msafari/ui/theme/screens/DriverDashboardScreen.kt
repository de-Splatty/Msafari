package com.adkins.msafari.ui.theme.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DriverDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToPastDrives: () -> Unit = {}
) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard", color = White) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Green) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigateToProfile()
                    },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNavigateToPastDrives()
                    },
                    label = { Text("Past Drives") },
                    icon = { Icon(Icons.Default.History, contentDescription = "Past Drives") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        onNavigateToPayments()
                    },
                    label = { Text("Payments") },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Payments") }
                )
            }
        },
        containerColor = Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, Driver!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (selectedTab) {
                0 -> {
                    if (hasLocationPermission) {
                        DriverMapScreen()
                    } else {
                        Text(
                            text = "Requesting location permission...",
                            color = White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
                        ) {
                            Text("Retry Permission")
                        }
                    }
                }
            }
        }
    }
}
