package com.adkins.msafari.ui.theme.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adkins.msafari.models.BookingData
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onContinue: (BookingData) -> Unit
) {
    val context = LocalContext.current
    val vehicleOptions = listOf("Van", "Bus", "Land Cruiser")
    var selectedVehicle by remember { mutableStateOf(vehicleOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    var numberOfTravelers by remember { mutableStateOf("") }
    var hasChildren by remember { mutableStateOf(false) }
    var numberOfChildren by remember { mutableStateOf("") }

    var travelDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var returnDate by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var pickupTime by remember { mutableStateOf("08:00") }
    var showError by remember { mutableStateOf(false) }

    val travelDatePicker = remember {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                travelDate = "%04d-%02d-%02d".format(year, month + 1, day)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val returnDatePicker = remember {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                returnDate = "%04d-%02d-%02d".format(year, month + 1, day)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePicker = remember {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                pickupTime = "%02d:%02d".format(hour, minute)
            },
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        )
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Booking", color = colorScheme.onPrimary) },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedVehicle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    vehicleOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedVehicle = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numberOfTravelers,
                onValueChange = { numberOfTravelers = it },
                label = { Text("Number of Travelers") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Has Children?", color = colorScheme.onBackground)
                Switch(
                    checked = hasChildren,
                    onCheckedChange = { hasChildren = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.primary,
                        uncheckedThumbColor = colorScheme.onBackground
                    )
                )
            }

            if (hasChildren) {
                OutlinedTextField(
                    value = numberOfChildren,
                    onValueChange = { numberOfChildren = it },
                    label = { Text("Number of Children") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { travelDatePicker.show() }) {
                    Text("Pick Travel Date")
                }
                Text(text = travelDate, color = colorScheme.onBackground)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { returnDatePicker.show() }) {
                    Text("Pick Return Date")
                }
                Text(text = returnDate, color = colorScheme.onBackground)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { timePicker.show() }) {
                    Text("Pick Pickup Time")
                }
                Text(text = pickupTime, color = colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val bookingData = BookingData(
                        travelDate = travelDate,
                        returnDate = returnDate,
                        vehicleType = selectedVehicle,
                        hasChildren = hasChildren,
                        numberOfTravelers = numberOfTravelers.toIntOrNull() ?: 0,
                        numberOfChildren = numberOfChildren.toIntOrNull() ?: 0,
                        travelers = emptyList()
                    )
                    if (bookingData.numberOfTravelers > 0) {
                        onContinue(bookingData)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }

            if (showError) {
                Text("Please fill all required fields properly.", color = colorScheme.error)
            }
        }
    }
}