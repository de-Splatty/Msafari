package com.adkins.msafari.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    onStartBooking: () -> Unit,
    onViewBookings: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit
)
 {
    var userName by remember { mutableStateOf("Client") }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Home, 1 = History, 2 = Profile

    LaunchedEffect(Unit) {
        AuthManager.fetchUserName(
            onResult = {
                userName = it
                isLoading = false
            },
            onError = {
                userName = "Client"
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Msafari", color = White, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green),
                actions = {
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = White)
                    }

                    IconButton(onClick = {
                        AuthManager.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = White)

                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Black) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Green) },
                    label = { Text("Home", color = White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Bookings", tint = Green) },
                    label = { Text("Bookings", color = White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Green) },
                    label = { Text("Profile", color = White) }
                )
            }
        },
        containerColor = Black
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> HomeTabContent(userName, isLoading, onStartBooking, onViewBookings)
                1 -> BookingHistoryScreen()
                2 -> ProfileTabContent(userName)
            }
        }
    }
}

@Composable
fun HomeTabContent(
    userName: String,
    isLoading: Boolean,
    onStartBooking: () -> Unit,
    onViewBookings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Green)
        } else {
            Text("Welcome, $userName", style = MaterialTheme.typography.headlineSmall, color = White)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartBooking,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("Start New Booking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewBookings,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("View Booking History")
            }
        }
    }
}

@Composable
fun ProfileTabContent(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = Green, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Profile: $userName", style = MaterialTheme.typography.titleLarge, color = White)
    }
}
