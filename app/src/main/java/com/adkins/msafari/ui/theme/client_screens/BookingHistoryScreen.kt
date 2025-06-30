package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.ClientScaffoldWrapper
import com.adkins.msafari.firestore.BookingManager
import com.adkins.msafari.models.Booking
import com.google.firebase.Timestamp

@Composable
fun BookingHistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = AuthManager.getCurrentUserId()
        if (userId != null) {
            BookingManager.getBookingsForUser(
                userId = userId,
                onSuccess = { bookingList ->
                    bookings = bookingList.mapNotNull { raw ->
                        try {
                            Booking(
                                clientId = raw["clientId"] as? String ?: return@mapNotNull null,
                                driverId = raw["driverId"] as? String ?: "",
                                vehicleType = raw["vehicleType"] as? String ?: "",
                                travelDate = raw["travelDate"] as? String ?: "",
                                returnDate = raw["returnDate"] as? String ?: "",
                                travelers = emptyList(),
                                totalPrice = (raw["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                                status = raw["status"] as? String ?: "pending",
                                approvedBy = raw["approvedBy"] as? String,
                                createdAt = raw["createdAt"] as? Timestamp ?: Timestamp.now(),
                                pickupLocation = raw["pickupLocation"] as? String ?: "N/A",
                                destinationLocation = raw["destinationLocation"] as? String ?: "N/A"
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

    ClientScaffoldWrapper(
        title = "Booking History",
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        showTripStatus = true,
        showProfile = true,
        content = { innerPadding ->
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
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    error != null -> {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    }

                    bookings.isEmpty() -> {
                        Text("No bookings found.", color = MaterialTheme.colorScheme.onBackground)
                    }

                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(bookings) { booking ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Vehicle: ${booking.vehicleType}")
                                        Text("Travelers: ${booking.travelers.size}")
                                        Text("Travel Date: ${booking.travelDate}")
                                        Text("Return Date: ${booking.returnDate}")
                                        Text("Pickup: ${booking.pickupLocation}")
                                        Text("Destination: ${booking.destinationLocation}")
                                        Text("Total: KES ${booking.totalPrice.toInt()}")
                                        Text(
                                            "Status: ${booking.status.replaceFirstChar { it.uppercase() }}",
                                            color = if (booking.status == "approved") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
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
    )
}