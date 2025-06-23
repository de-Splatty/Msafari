package com.adkins.msafari.ui.theme.screens

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
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    onBack: () -> Unit = {}
) {
    var bookings by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            BookingManager.getBookingsForUser(
                userId = userId,
                onSuccess = {
                    bookings = it
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
                title = { Text("Booking History", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Black)
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
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Green)
                    }
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
                bookings.isEmpty() -> {
                    Text("No bookings found.", color = White)
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bookings) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Black),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Driver ID: ${booking["driverId"] ?: "N/A"}", color = Green)
                                    Text("Vehicle: ${booking["vehicleType"] ?: "N/A"}", color = White)
                                    Text("Travelers: ${booking["travelersCount"] ?: "Unknown"}", color = White)
                                    Text("Travel Date: ${booking["travelDate"] ?: "N/A"}", color = White)
                                    Text("Return Date: ${booking["returnDate"] ?: "N/A"}", color = White)
                                    Text("Booked On: ${booking["timestamp"] ?: "N/A"}", color = White)
                                    Text("Status: Confirmed", color = Green, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
