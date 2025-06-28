package com.adkins.msafari.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileCompletionScreen(
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Land Cruiser") }
    var seatCount by remember { mutableStateOf("") }
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }
    var profilePicUrl by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val vehicleTypes = listOf("Land Cruiser", "Van", "Bus")
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profilePicUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Profile", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (profilePicUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { launcher.launch("image/*") }
                    )
                } else {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Upload Profile Picture")
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name", color = White) },
                colors = driverTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it },
                label = { Text("Vehicle Plate Number", color = White) },
                colors = driverTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle Type", color = White) },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = White)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    colors = driverTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                vehicleType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = seatCount,
                onValueChange = { seatCount = it },
                label = { Text("Number of Seats", color = White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = driverTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

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
                        if (name.isBlank() || plateNumber.isBlank() || seatCount.isBlank() || profilePicUri == null) {
                            errorMessage = "Please fill in all fields and upload a photo"
                        } else {
                            errorMessage = null
                            isSubmitting = true

                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                            val fileName = "profile_pics/$uid-${UUID.randomUUID()}"
                            val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

                            storageRef.putFile(profilePicUri!!)
                                .continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        throw task.exception ?: Exception("Upload failed")
                                    }
                                    storageRef.downloadUrl
                                }
                                .addOnSuccessListener { downloadUri ->
                                    profilePicUrl = downloadUri.toString()

                                    val driverData = mapOf(
                                        "name" to name,
                                        "plateNumber" to plateNumber,
                                        "vehicleType" to vehicleType,
                                        "seater" to seatCount.toInt(),
                                        "profilePicUrl" to profilePicUrl,
                                        "uid" to uid
                                    )

                                    FirebaseFirestore.getInstance()
                                        .collection("drivers")
                                        .document(uid)
                                        .set(driverData)
                                        .addOnSuccessListener {
                                            isSubmitting = false
                                            onSubmit()
                                        }
                                        .addOnFailureListener {
                                            isSubmitting = false
                                            errorMessage = "Failed to save data"
                                        }
                                }
                                .addOnFailureListener {
                                    isSubmitting = false
                                    errorMessage = "Image upload failed"
                                }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
                ) {
                    Text(if (isSubmitting) "Submitting..." else "Submit")
                }
            }
        }
    }
}

@Composable
fun driverTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = White,
    unfocusedTextColor = White,
    focusedLabelColor = Green,
    unfocusedLabelColor = White,
    cursorColor = Green
)
