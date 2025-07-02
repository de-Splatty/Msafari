package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun PastDrivesScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    DriverScaffoldWrapper(
        title = "Past Drives",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Past Drives",
                color = Green,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "List of completed safaris will show here.",
                color = White,
                fontSize = 16.sp
            )
        }
    }
}