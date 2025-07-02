package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DriverNewRequestsScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToDetails: (bookingId: String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val driverId = auth.currentUser?.uid.orEmpty()

    var bookings by remember { mutableStateOf(emptyList<Booking>()) }
    var isRefreshing by remember { mutableStateOf(false) }

    fun loadBookings() {
        isRefreshing = true
        db.collection("bookings")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                bookings = result.documents.mapNotNull { doc ->
                    val booking = doc.toObject(Booking::class.java)?.copy(id = doc.id)
                    // Only show if this booking was sent to this driver
                    val approvedBy = doc.getString("approvedBy")
                    val rejectedBy = doc.getString("rejectedBy")
                    if (approvedBy == null && rejectedBy != driverId) {
                        booking
                    } else null
                }
                isRefreshing = false
            }
            .addOnFailureListener {
                isRefreshing = false
            }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { loadBookings() }
    )

    LaunchedEffect(Unit) {
        loadBookings()
    }

    DriverScaffoldWrapper(
        title = "New Requests",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .background(Black)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${booking.pickupLocation} → ${booking.destinationLocation}",
                                color = Green,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Date: ${booking.travelDate} to ${booking.returnDate}",
                                color = White,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Vehicle: ${booking.vehicleType}",
                                color = White,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Total: KES ${booking.totalPrice}",
                                color = White,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        db.collection("bookings").document(booking.id)
                                            .update(
                                                mapOf(
                                                    "status" to "approved",
                                                    "approvedBy" to driverId,
                                                    "driverId" to driverId // ✅ This was missing!
                                                )
                                            )
                                            .addOnSuccessListener {
                                                // Optionally delete from other drivers' booking_requests
                                                loadBookings()
                                            }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                                ) {
                                    Text("Approve", color = Black)
                                }

                                OutlinedButton(
                                    onClick = {
                                        db.collection("bookings").document(booking.id)
                                            .update("rejectedBy", driverId)
                                            .addOnSuccessListener { loadBookings() }
                                    }
                                ) {
                                    Text("Reject", color = White)
                                }

                                TextButton(
                                    onClick = { onNavigateToDetails(booking.id) }
                                ) {
                                    Text("View", color = White)
                                }
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Green
            )
        }
    }
}