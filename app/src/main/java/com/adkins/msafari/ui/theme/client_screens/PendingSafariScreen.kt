package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.firestore.BookingManager
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingSafarisScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var bookings by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            BookingManager.getBookingsForUser(
                userId = userId,
                onSuccess = { bookingList ->
                    bookings = bookingList.filter { it["status"] == "pending" }
                    isLoading = false
                },
                onFailure = {
                    error = it
                    isLoading = false
                }
            )
        } else {
            error = "User not logged in"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Safaris", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate(Screen.ClientHome.route) // or Screen.ClientTripStatus.route
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        },
        containerColor = Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green)
                    }
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                bookings.isEmpty() -> {
                    Text(
                        text = "No pending safaris found.",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bookings) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Vehicle: ${booking["vehicleType"] ?: "N/A"}")
                                    Text("Travel Date: ${booking["travelDate"] ?: "N/A"}")
                                    Text("Return Date: ${booking["returnDate"] ?: "N/A"}")
                                    Text("Status: Pending", color = Green)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}