package com.adkins.msafari.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.firestore.BookingManager
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.models.BookingData
import com.adkins.msafari.models.Traveler
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    selectedDrivers: List<Driver>,
    bookingData: BookingData,
    travelers: List<Traveler>,
    onBookingSuccess: () -> Unit,
    onBookingFailure: (String) -> Unit,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green,
                    titleContentColor = Black
                )
            )
        },
        containerColor = Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You selected ${selectedDrivers.size} drivers", color = White)

            selectedDrivers.forEach { driver ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Black)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${driver.name}", color = White)
                        Text("Vehicle: ${driver.vehicleType}", color = White)
                        Text("Seats: ${driver.seater}", color = White)
                    }
                }
            }

            Divider(color = Green)
            Text("Travel Date: ${bookingData.travelDate}", color = White)
            Text("Return Date: ${bookingData.returnDate}", color = White)
            Text("Travelers: ${travelers.size}", color = White)
            Text("Children Present: ${if (bookingData.hasChildren) "Yes" else "No"}", color = White)

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Green)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            val userId = AuthManager.getCurrentUserId()
                            if (userId != null) {
                                try {
                                    val days = ChronoUnit.DAYS.between(
                                        LocalDate.parse(bookingData.travelDate),
                                        LocalDate.parse(bookingData.returnDate)
                                    ).toInt().coerceAtLeast(1)

                                    val dailyRate = 18000
                                    val totalCost = days * dailyRate
                                    val commission = (totalCost * 0.20).toInt()

                                    BookingManager.saveBookingToSelectedDrivers(
                                        clientId = userId,
                                        selectedDrivers = selectedDrivers,
                                        bookingData = bookingData,
                                        travelers = travelers,
                                        totalCost = totalCost,
                                        commission = commission,
                                        onSuccess = {
                                            isLoading = false
                                            onBookingSuccess()
                                        },
                                        onFailure = { error ->
                                            isLoading = false
                                            onBookingFailure(error)
                                        }
                                    )
                                } catch (e: Exception) {
                                    isLoading = false
                                    onBookingFailure("Error calculating cost: ${e.localizedMessage}")
                                }
                            } else {
                                isLoading = false
                                onBookingFailure("User not logged in")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}