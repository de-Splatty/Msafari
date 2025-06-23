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
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Booking", color = White) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vehicle Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedVehicle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle Type", color = White) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedLabelColor = Green,
                        unfocusedLabelColor = White,
                        cursorColor = Green
                    )
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

            // Number of Adults
            OutlinedTextField(
                value = numberOfTravelers,
                onValueChange = { numberOfTravelers = it },
                label = { Text("Number of Adults", color = White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedLabelColor = Green,
                    unfocusedLabelColor = White,
                    cursorColor = Green
                )
            )

            // Has Children Switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Are there children?", color = White)
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = hasChildren,
                    onCheckedChange = {
                        hasChildren = it
                        if (!it) numberOfChildren = ""
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        checkedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = White.copy(alpha = 0.3f)
                    )
                )
            }

            // Number of Children
            if (hasChildren) {
                OutlinedTextField(
                    value = numberOfChildren,
                    onValueChange = { numberOfChildren = it },
                    label = { Text("How many children?", color = White) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedLabelColor = Green,
                        unfocusedLabelColor = White,
                        cursorColor = Green
                    )
                )
            }

            // Dates and Time
            Button(
                onClick = { travelDatePicker.show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("Travel Date: $travelDate")
            }

            Button(
                onClick = { returnDatePicker.show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("Return Date: $returnDate")
            }

            Button(
                onClick = { timePicker.show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("Pickup Time: $pickupTime")
            }

            if (showError) {
                Text(
                    "Please fill all fields correctly",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val adults = numberOfTravelers.toIntOrNull()
                    val kids = if (hasChildren) numberOfChildren.toIntOrNull() ?: 0 else 0
                    if (adults == null || adults <= 0 || (hasChildren && numberOfChildren.isBlank()) || travelDate.isBlank() || returnDate.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        onContinue(
                            BookingData(
                                vehicleType = selectedVehicle,
                                numberOfTravelers = adults + kids,
                                hasChildren = hasChildren,
                                numberOfChildren = kids,
                                travelDate = travelDate,
                                returnDate = returnDate,
                                travelers = emptyList() // travelers will be added later
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Text("Continue")
            }
        }
    }
}