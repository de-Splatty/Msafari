package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.models.Traveler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableDriversScreen(
    travelDate: String,
    returnDate: String,
    travelers: List<Traveler>,
    onContinue: (List<Driver>) -> Unit,
    onBack: () -> Unit
) {
    var drivers by remember { mutableStateOf<List<Driver>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val selectedDrivers = remember { mutableStateListOf<Driver>() }

    LaunchedEffect(Unit) {
        DriverManager.fetchAvailableDrivers(
            travelDate = travelDate,
            returnDate = returnDate,
            onSuccess = {
                drivers = it
                isLoading = false
                error = null
            },
            onFailure = { msg ->
                error = msg
                isLoading = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Available Drivers", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            if (selectedDrivers.size == 3) {
                Button(
                    onClick = { onContinue(selectedDrivers) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Continue with Selected Drivers")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }

                drivers.isEmpty() -> {
                    Text("No drivers available for the selected dates.", color = MaterialTheme.colorScheme.onBackground)
                }

                else -> {
                    LazyColumn {
                        items(drivers) { driver ->
                            val isSelected = selectedDrivers.contains(driver)
                            DriverCard(
                                driver = driver,
                                isSelected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedDrivers.remove(driver)
                                    } else if (selectedDrivers.size < 3) {
                                        selectedDrivers.add(driver)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverCard(driver: Driver, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(driver.name, fontWeight = FontWeight.Bold)
            Text("Vehicle: ${driver.vehicleType}")
            Text("Seats: ${driver.seater}")
            Text("Plate No: ${driver.plateNumber}")
        }
    }
}