package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.firestore.Driver
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DriverProfileScreen(
    onLogout: () -> Unit,
    onSwitchAccount: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var driver by remember { mutableStateOf<Driver?>(null) }
    var loading by remember { mutableStateOf(true) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid != null) {
            DriverManager.getDriverProfile(uid) { profile ->
                driver = profile
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
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
            CircularProgressIndicator(color = Green, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (driver != null) {
            ProfileItem(label = "Name", value = driver!!.name)
            ProfileItem(label = "Phone", value = driver!!.phoneNumber)
            ProfileItem(label = "National ID", value = driver!!.nationalId)
            ProfileItem(label = "Vehicle Type", value = driver!!.vehicleType)
            ProfileItem(label = "Plate Number", value = driver!!.plateNumber)
            ProfileItem(label = "Seater", value = driver!!.seater.toString())
        } else {
            Text(
                text = "Profile not found.",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(text = "Go to Dashboard", icon = Icons.Default.DirectionsCar, onClick = onNavigateToDashboard)
        ActionButton(text = "Switch Account", icon = Icons.Default.SwitchAccount, onClick = onSwitchAccount)
        ActionButton(text = "Logout", icon = Icons.Default.ExitToApp, onClick = onLogout)
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = White, fontSize = 16.sp)
    }
}

@Composable
fun ActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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