package com.adkins.msafari.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    onStartBooking: () -> Unit,
    onViewHistory: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Msafari",
                        fontSize = 20.sp,
                        color = White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green
                ),
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Black)
                    }
                }
            )
        },
        containerColor = Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onStartBooking,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Black)
            ) {
                Icon(Icons.Default.Tour, contentDescription = "New Booking")
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Booking")
            }

            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
            ) {
                Icon(Icons.Default.History, contentDescription = "Booking History")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Booking History")
            }
        }
    }
}