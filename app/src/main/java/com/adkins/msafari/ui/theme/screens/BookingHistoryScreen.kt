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
import com.adkins.msafari.models.BookingData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    onBack: () -> Unit = {}
) {
    var bookings by remember { mutableStateOf<List<BookingData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            BookingManager.getBookingsForUser(
                userId = userId,
                onSuccess = { bookingList ->
                    bookings = bookingList.mapNotNull { raw ->
                        try {
                            BookingData(
                                travelDate = raw["travelDate"] as? String ?: return@mapNotNull null,
                                returnDate = raw["returnDate"] as? String ?: return@mapNotNull null,
                                vehicleType = raw["vehicleType"] as? String ?: return@mapNotNull null,
                                hasChildren = raw["hasChildren"] as? Boolean ?: false,
                                numberOfTravelers = (raw["numberOfTravelers"] as? Long)?.toInt() ?: 0,
                                numberOfChildren = (raw["numberOfChildren"] as? Long)?.toInt() ?: 0,
                                travelers = emptyList()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
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
                title = { Text("Booking History", color = colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
            )
        },
        containerColor = colorScheme.background
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
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
                error != null -> {
                    Text("Error: $error", color = colorScheme.error)
                }
                bookings.isEmpty() -> {
                    Text("No bookings found.", color = colorScheme.onBackground)
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(bookings) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Vehicle: ${booking.vehicleType}", color = colorScheme.onSurface)
                                    Text("Travelers: ${booking.numberOfTravelers}", color = colorScheme.onSurface)
                                    Text("Travel Date: ${booking.travelDate}", color = colorScheme.onSurface)
                                    Text("Return Date: ${booking.returnDate}", color = colorScheme.onSurface)
                                    Text("Children: ${if (booking.hasChildren) booking.numberOfChildren else 0}", color = colorScheme.onSurface)
                                    Text(
                                        "Status: Confirmed",
                                        color = colorScheme.primary,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}