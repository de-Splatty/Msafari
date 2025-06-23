package com.adkins.msafari.ui.theme.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.models.Traveler
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

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
    val selectedDrivers = remember { mutableStateListOf<Driver>() }

    LaunchedEffect(Unit) {
        DriverManager.fetchAvailableDrivers(
            travelDate = travelDate,
            returnDate = returnDate,
            onSuccess = {
                drivers = it
                isLoading = false
            },
            onFailure = {
                isLoading = false
            }
        )
    }

    Scaffold(
        containerColor = Black,
        topBar = {
            TopAppBar(
                title = { Text("Available Drivers", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green,
                    titleContentColor = Black
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
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green)
                }
            } else {
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

@Composable
fun DriverCard(driver: Driver, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Green else Color.Transparent
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Black,
            contentColor = White
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = driver.profilePicUrl,
                contentDescription = "Driver Image",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(driver.name, fontWeight = FontWeight.Bold, color = White)
                Text("Vehicle: ${driver.vehicleType}", color = White)
                Text("Seats: ${driver.seater}", color = White)
            }
        }
    }
}
