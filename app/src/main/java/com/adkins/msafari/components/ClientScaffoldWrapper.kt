package com.adkins.msafari.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.adkins.msafari.ui.components.BottomNavBar
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScaffoldWrapper(
    title: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    showTripStatus: Boolean = false, // ✅ New flag with default value
    content: @Composable (PaddingValues) -> Unit,
    showProfile: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                showTripStatus = showTripStatus // ✅ Pass the flag here
            )
        },
        containerColor = Black,
        content = content
    )
}