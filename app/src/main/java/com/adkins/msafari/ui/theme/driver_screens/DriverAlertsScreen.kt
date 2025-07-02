package com.adkins.msafari.ui.theme.driver_screens

import android.R.attr.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun DriverAlertsScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var alerts by remember { mutableStateOf(listOf<String>()) }

    // Fake loading simulation
    LaunchedEffect(Unit) {
        // Simulate loading delay
        kotlinx.coroutines.delay(1000)
        alerts = listOf(
            "Upcoming Safari: July 2nd - Nairobi to Nakuru",
            "Payment Received: KES 14,400 from Client A",
            "Rating Received: 4.8 stars from Client B"
        )
        isLoading = false
    }

    DriverScaffoldWrapper(
        title = "Driver Alerts",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Green,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                if (alerts.isEmpty()) {
                    Text(
                        text = "No new alerts.",
                        color = White,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    alerts.forEach { alert ->
                        Text(
                            text = "• $alert",
                            color = White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}