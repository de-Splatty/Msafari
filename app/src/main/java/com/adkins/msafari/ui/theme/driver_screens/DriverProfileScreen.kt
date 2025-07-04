package com.adkins.msafari.ui.theme.driver_screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.firestore.UserManager
import com.adkins.msafari.models.User
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onSwitchAccount: () -> Unit
) {
    var driver by remember { mutableStateOf<Driver?>(null) }
    var loading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newRateInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currentAccount = remember { AccountManager.getCurrentAccount() }
    val uid = currentAccount?.uid
    val savedAccounts = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        if (uid != null) {
            DriverManager.getDriverProfile(uid) {
                driver = it
                loading = false
            }
        } else {
            loading = false
        }

        AccountManager.init(context)
        savedAccounts.clear()
        savedAccounts.addAll(AccountManager.getSavedAccounts())
    }

    DriverScaffoldWrapper(
        title = "Driver Profile",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Driver Profile",
                color = Green,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(
                    color = Green,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (driver != null) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { SectionTitle("Personal Info") }
                    item { ProfileItem("Name", driver!!.name) }
                    item { ProfileItem("Phone", driver!!.phoneNumber) }
                    item { ProfileItem("National ID", driver!!.nationalId) }

                    item { SectionTitle("Vehicle Info") }
                    item { ProfileItem("Vehicle Type", driver!!.vehicleType) }
                    item { ProfileItem("Plate Number", driver!!.plateNumber) }
                    item { ProfileItem("Seater", driver!!.seater.toString()) }

                    item { SectionTitle("Daily Rate") }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("KES ${driver!!.dailyRate}", color = White, fontSize = 16.sp)
                                Text(
                                    "Last Updated: ${formatDate(driver!!.lastRateUpdate)}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    val now = System.currentTimeMillis()
                                    val canEdit = now - driver!!.lastRateUpdate >= 14 * 24 * 60 * 60 * 1000L
                                    if (canEdit) {
                                        showEditDialog = true
                                        newRateInput = driver!!.dailyRate.toString()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Rate can only be edited every 14 days.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Rate", tint = Green)
                            }
                        }
                    }

                    item {
                        Column {
                            Button(
                                onClick = { dropdownExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Switch Account", color = White)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = White)
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.background(Black)
                            ) {
                                if (savedAccounts.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Login to Existing Account", color = White) },
                                        onClick = {
                                            dropdownExpanded = false
                                            onSwitchAccount()
                                        }
                                    )
                                } else {
                                    savedAccounts.forEach { account ->
                                        val displayName = account.name?.takeIf { it.isNotBlank() } ?: "Unnamed"
                                        val displayRole = account.role?.replaceFirstChar { it.uppercase() } ?: "Unknown Role"

                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(displayName, color = White, fontWeight = FontWeight.SemiBold)
                                                    Text(
                                                        displayRole,
                                                        fontSize = 12.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            },
                                            onClick = {
                                                dropdownExpanded = false
                                                AccountManager.setCurrentAccount(account)
                                                UserManager.saveUserToFirestore(account)
                                                when (account.role?.lowercase()) {
                                                    "client" -> onNavigate(Screen.ClientHome.route)
                                                    "driver" -> onNavigate(Screen.DriverDashboard.route)
                                                    else -> onNavigate(Screen.Login.route)
                                                }
                                            }
                                        )
                                    }

                                    Divider()

                                    DropdownMenuItem(
                                        text = { Text("Login to Another Account", color = White) },
                                        onClick = {
                                            dropdownExpanded = false
                                            onSwitchAccount()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Profile not found.",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    AuthManager.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = White)
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val rate = newRateInput.toIntOrNull()
                    if (rate != null && uid != null) {
                        DriverManager.updateDailyRate(
                            driverId = uid,
                            newRate = rate,
                            onSuccess = {
                                Toast.makeText(context, "Rate updated!", Toast.LENGTH_SHORT).show()
                                showEditDialog = false
                                DriverManager.getDriverProfile(uid) {
                                    driver = it
                                }
                            },
                            onFailure = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Invalid rate entered.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save", color = Green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = { Text("Edit Daily Rate", color = White) },
            text = {
                OutlinedTextField(
                    value = newRateInput,
                    onValueChange = { newRateInput = it },
                    label = { Text("New Rate (KES)", color = Color.Gray) },
                    singleLine = true
                )
            },
            containerColor = Black
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, color = Green, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}