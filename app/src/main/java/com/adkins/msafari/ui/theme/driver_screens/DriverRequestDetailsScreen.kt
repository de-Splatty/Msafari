package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.models.Booking
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun DriverRequestDetailsScreen(
    bookingId: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var booking by remember { mutableStateOf<Booking?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) {
            error = "Invalid booking ID"
            loading = false
            return@LaunchedEffect
        }

        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(bookingId)
                .get()
                .await()

            booking = snapshot.toObject(Booking::class.java)?.copy(id = snapshot.id)

            if (booking == null) {
                error = "Booking not found"
            }
        } catch (e: Exception) {
            error = "Error fetching booking: ${e.localizedMessage}"
        } finally {
            loading = false
        }
    }

    DriverScaffoldWrapper(
        title = "Booking Details",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(innerPadding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        color = Green,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                booking != null -> {
                    booking?.let {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("From: ${it.pickupLocation}", color = White)
                            Text("To: ${it.destinationLocation}", color = White)
                            Text("Dates: ${it.travelDate} → ${it.returnDate}", color = White)
                            Text("Vehicle: ${it.vehicleType}", color = White)
                            Text("Total Price: KES ${it.totalPrice}", color = White)
                            Text("Client ID: ${it.clientId}", color = White)
                            Text("Driver ID: ${it.driverId}", color = White)
                            Text("Status: ${it.status}", color = White)
                            Text("Approved By: ${it.approvedBy ?: "Not yet approved"}", color = White)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = Green)
                            ) {
                                Text("Back", color = Black)
                            }
                        }
                    }
                }
            }
        }
    }
}