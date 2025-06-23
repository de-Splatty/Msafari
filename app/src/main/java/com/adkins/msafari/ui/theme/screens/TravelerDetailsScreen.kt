package com.adkins.msafari.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adkins.msafari.models.Traveler
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelerDetailsScreen(
    numberOfTravelers: Int,
    hasChildren: Boolean,
    numberOfChildren: Int,
    onSubmit: (List<Traveler>) -> Unit,
    onBack: () -> Unit
) {
    val safeChildren = if (hasChildren) numberOfChildren.coerceAtMost(numberOfTravelers) else 0
    val adultCount = numberOfTravelers - safeChildren

    val travelerInputs = remember {
        mutableStateListOf<Traveler>().apply {
            repeat(numberOfTravelers) {
                add(Traveler(name = "", age = 0, idNumber = ""))
            }
        }
    }

    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Traveler Details", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green,
                    titleContentColor = Black
                )
            )
        },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(travelerInputs) { index, traveler ->
                    val isChild = hasChildren && index >= adultCount

                    Text(
                        text = if (isChild) "Child ${index - adultCount + 1}" else "Traveler ${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = White
                    )

                    OutlinedTextField(
                        value = traveler.name,
                        onValueChange = { travelerInputs[index] = traveler.copy(name = it) },
                        label = { Text("Full Name", color = White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedLabelColor = Green,
                            unfocusedLabelColor = White,
                            cursorColor = Green
                        )
                    )

                    OutlinedTextField(
                        value = if (traveler.age == 0) "" else traveler.age.toString(),
                        onValueChange = { newValue ->
                            val newAge = newValue.filter { it.isDigit() }.toIntOrNull() ?: 0
                            travelerInputs[index] = traveler.copy(age = newAge)
                        },
                        label = { Text("Age", color = White) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedLabelColor = Green,
                            unfocusedLabelColor = White,
                            cursorColor = Green
                        )
                    )

                    if (!isChild) {
                        OutlinedTextField(
                            value = traveler.idNumber,
                            onValueChange = { travelerInputs[index] = traveler.copy(idNumber = it) },
                            label = { Text("ID Number", color = White) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedLabelColor = Green,
                                unfocusedLabelColor = White,
                                cursorColor = Green
                            )
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .background(White.copy(alpha = 0.2f))
                    )
                }
            }

            // Error display
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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
                        val isInvalid = travelerInputs.anyIndexed { index, t ->
                            t.name.isBlank() || t.age <= 0 ||
                                    (!hasChildren || index < adultCount) && t.idNumber.isBlank()
                        }

                        if (isInvalid) {
                            error = "Please fill all required fields correctly."
                        } else {
                            error = null
                            onSubmit(travelerInputs)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

// Helper extension
inline fun <T> List<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    for (i in indices) if (predicate(i, this[i])) return true
    return false
}
