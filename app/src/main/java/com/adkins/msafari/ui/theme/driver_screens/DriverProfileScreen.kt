package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.adkins.msafari.components.DriverScaffoldWrapper

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

    val currentAccount = remember { AccountManager.getCurrentAccount() }
    val uid = currentAccount?.uid
    val otherAccounts = remember {
        AccountManager.getSavedAccounts().filter { acc ->
            currentAccount?.uid != null && acc.uid != currentAccount.uid
        }
    }

    LaunchedEffect(uid) {
        if (uid != null) {
            DriverManager.getDriverProfile(uid) { profile ->
                driver = profile
                loading = false
            }
        } else {
            loading = false
        }
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
                SectionTitle("Personal Info")
                ProfileItem("Name", driver!!.name)
                ProfileItem("Phone", driver!!.phoneNumber)
                ProfileItem("National ID", driver!!.nationalId)

                Spacer(modifier = Modifier.height(12.dp))

                SectionTitle("Vehicle Info")
                ProfileItem("Vehicle Type", driver!!.vehicleType)
                ProfileItem("Plate Number", driver!!.plateNumber)
                ProfileItem("Seater", driver!!.seater.toString())
            } else {
                Text(
                    text = "Profile not found.",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (otherAccounts.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { dropdownExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwitchAccount, contentDescription = "Switch", tint = Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Account", color = Black)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(Black)
                    ) {
                        otherAccounts.forEach { account ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${account.name} (${account.role})",
                                        color = White
                                    )
                                },
                                onClick = {
                                    try {
                                        AccountManager.setCurrentAccount(account)
                                        dropdownExpanded = false
                                        onSwitchAccount()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        dropdownExpanded = false
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            ActionButton("Logout", Icons.Default.ExitToApp, onClick = onLogout)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Green,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = White, fontSize = 16.sp)
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Green),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Black)
    }
}