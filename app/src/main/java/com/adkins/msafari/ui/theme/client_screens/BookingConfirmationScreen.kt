package com.adkins.msafari.ui.theme.client_screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.adkins.msafari.utils.ErrorLogger
import com.adkins.msafari.viewmodels.BookingViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    selectedDrivers: List<Driver>,
    bookingData: BookingData,
    travelers: List<Traveler>,
    bookingViewModel: BookingViewModel,
    onBookingSuccess: () -> Unit,
    onBookingFailure: (String) -> Unit,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking", color = colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = colorScheme.onPrimary
                )
            )
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You selected ${selectedDrivers.size} drivers", color = colorScheme.onBackground)

            selectedDrivers.forEach { driver ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${driver.name}", color = colorScheme.onSurface)
                        Text("Vehicle: ${driver.vehicleType}", color = colorScheme.onSurface)
                        Text("Seats: ${driver.seater}", color = colorScheme.onSurface)
                        Text("Rate: ${driver.dailyRate} KES/day", color = colorScheme.onSurface)
                    }
                }
            }

            Divider(color = colorScheme.primary)

            Text("Pickup Location: ${bookingData.pickupLocation}", color = colorScheme.onBackground)
            Text("Destination: ${bookingData.destinationLocation}", color = colorScheme.onBackground)
            Text("Travel Date: ${bookingData.travelDate}", color = colorScheme.onBackground)
            Text("Return Date: ${bookingData.returnDate}", color = colorScheme.onBackground)
            Text("Travelers: ${travelers.size}", color = colorScheme.onBackground)
            Text("Children Present: ${if (bookingData.hasChildren) "Yes" else "No"}", color = colorScheme.onBackground)

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = colorScheme.primary)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary)
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

                                    val totalCost = selectedDrivers.sumOf { it.dailyRate * days }
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
                                            bookingViewModel.setActiveTrip(true)
                                            onBookingSuccess()
                                        },
                                        onFailure = { error ->
                                            isLoading = false
                                            ErrorLogger.logError("Booking save failed", Exception(error), userId)
                                            onBookingFailure(error)
                                        }
                                    )
                                } catch (e: Exception) {
                                    isLoading = false
                                    ErrorLogger.logError("Cost calculation failed", e, userId)
                                    onBookingFailure("Error calculating cost: ${e.localizedMessage}")
                                }
                            } else {
                                isLoading = false
                                ErrorLogger.logError("User not logged in during booking", null)
                                onBookingFailure("User not logged in")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}