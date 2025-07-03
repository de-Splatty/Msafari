package com.adkins.msafari.ui.theme.driver_screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RateSetupScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val uid = AccountManager.getCurrentAccount()?.uid
    var rateInput by remember { mutableStateOf("") }
    var currentRate by remember { mutableStateOf(0) }
    var lastUpdatedAt by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val isEditable = remember(lastUpdatedAt) {
        if (lastUpdatedAt == null) return@remember true
        val last = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastUpdatedAt!!), ZoneId.systemDefault())
        val now = LocalDateTime.now()
        ChronoUnit.DAYS.between(last, now) >= 14
    }

    LaunchedEffect(uid) {
        if (uid != null) {
            DriverManager.getDriverProfile(uid) { driver ->
                currentRate = driver?.dailyRate ?: 0
                lastUpdatedAt = driver?.lastRateUpdate
                rateInput = if (currentRate > 0) currentRate.toString() else ""
            }
        }
    }

    DriverScaffoldWrapper(
        title = "Set Daily Rate",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Set Your Daily Rate", fontSize = 22.sp, color = Green, fontWeight = FontWeight.Bold)

            if (currentRate > 0) {
                Text("Current Rate: KES $currentRate", color = White, fontSize = 16.sp)
                lastUpdatedAt?.let {
                    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                    Text("Last Updated: $date", color = Color.Gray, fontSize = 14.sp)
                }
            }

            OutlinedTextField(
                value = rateInput,
                onValueChange = { rateInput = it.filter { ch -> ch.isDigit() } },
                label = { Text("Daily Rate (KES)") },
                singleLine = true,
                enabled = isEditable,
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            successMessage?.let {
                Text(it, color = Green, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    val newRate = rateInput.toIntOrNull()
                    if (uid != null && newRate != null && newRate > 0) {
                        DriverManager.updateDailyRate(uid, newRate,
                            onSuccess = {
                                successMessage = "Rate updated to KES $newRate."
                                errorMessage = null
                                currentRate = newRate
                                lastUpdatedAt = System.currentTimeMillis()
                            },
                            onFailure = {
                                errorMessage = it
                                successMessage = null
                            }
                        )
                    } else {
                        errorMessage = "Please enter a valid number."
                        successMessage = null
                    }
                },
                enabled = isEditable,
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save", color = Black)
            }

            if (!isEditable) {
                Text(
                    "You can update this rate again after 14 days.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}