package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.adkins.msafari.viewmodels.DriverViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverInfoScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: DriverViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }
    val vehicleTypes = listOf("Land Cruiser", "Van", "Bus")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Driver Info", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Black)
            )
        },
        containerColor = Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.fullName,
                onValueChange = { viewModel.updateField(fullName = it) },
                label = { Text("Full Name") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.updateField(phoneNumber = it) },
                label = { Text("Phone Number") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.idNumber,
                onValueChange = { viewModel.updateField(idNumber = it) },
                label = { Text("ID/License Number") },
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = vehicleDropdownExpanded,
                onExpandedChange = { vehicleDropdownExpanded = !vehicleDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = state.vehicleType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleDropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = vehicleDropdownExpanded,
                    onDismissRequest = { vehicleDropdownExpanded = false }
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.updateField(vehicleType = type)
                                vehicleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.plateNumber,
                onValueChange = { viewModel.updateField(plateNumber = it) },
                label = { Text("Plate Number") },
                singleLine = true
            )

            OutlinedTextField(
                value = if (state.seater == 0) "" else state.seater.toString(),
                onValueChange = { value ->
                    val seats = value.toIntOrNull() ?: 0
                    viewModel.updateField(seater = seats)
                },
                label = { Text("Number of Seats") },
                singleLine = true
            )

            OutlinedTextField(
                value = if (state.dailyRate == 0) "" else state.dailyRate.toString(),
                onValueChange = { value ->
                    val rate = value.toIntOrNull() ?: 0
                    viewModel.updateField(dailyRate = rate)
                },
                label = { Text("Daily Rate (KES)") },
                singleLine = true
            )

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.submitDriverInfo {
                        onNavigate("driver_dashboard")
                    }
                },
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Info", color = Black)
            }
        }
    }

    // Auto navigate if already complete
    LaunchedEffect(Unit) {
        viewModel.loadDriverIfExists {
            onNavigate("driver_dashboard")
        }
    }
}