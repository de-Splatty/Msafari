package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.R
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.ClientScaffoldWrapper
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Green
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ClientHomeScreen(
    onStartBooking: () -> Unit,
    onViewHistory: () -> Unit,
    onSettings: () -> Unit,
    onProfileClick: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val carouselImages = listOf(
        R.drawable.antelope,
        R.drawable.car,
        R.drawable.cruiser,
        R.drawable.elephant,
        R.drawable.wildbeast
    )
    val pagerState = rememberPagerState()
    var userName by remember { mutableStateOf("Traveler") }

    val tripActive = remember { mutableStateOf(true) }

    // Fetch user name
    LaunchedEffect(Unit) {
        AuthManager.fetchUserName(
            onResult = { name -> userName = name },
            onError = { userName = "Traveler" }
        )
    }

    // Auto-scroll images
    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        val nextPage = (pagerState.currentPage + 1) % carouselImages.size
        pagerState.animateScrollToPage(nextPage)
    }

    ClientScaffoldWrapper(
        title = "Msafari",
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        showTripStatus = tripActive.value,
        showProfile = true, // ✅ Fixed
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome, $userName!",
                    fontSize = 20.sp,
                    color = colors.onBackground
                )

                HorizontalPager(
                    count = carouselImages.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    Image(
                        painter = painterResource(id = carouselImages[page]),
                        contentDescription = "Safari Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    activeColor = Green,
                    inactiveColor = colors.outline
                )

                Button(
                    onClick = onStartBooking,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Tour, contentDescription = "New Booking")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Booking", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { onNavigate(Screen.PendingSafaris.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
                ) {
                    Icon(Icons.Default.History, contentDescription = "Pending Safaris")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pending Safaris", fontSize = 16.sp)
                }


            }
        }
    )
}