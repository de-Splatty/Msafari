package com.adkins.msafari.ui.theme.driver_screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var dailyRateSet by remember { mutableStateOf(false) }
    var showReminder by remember { mutableStateOf(false) }
    val currentAccount = remember { AccountManager.getCurrentAccount() }

    val sharedPrefs = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    val skipReminder = sharedPrefs.getBoolean("skip_rate_reminder", false)

    LaunchedEffect(Unit) {
        currentAccount?.uid?.let { uid ->
            DriverManager.getDriverProfile(uid) { driver ->
                val rate = driver?.dailyRate ?: 0
                dailyRateSet = rate > 0
                showReminder = !dailyRateSet && !skipReminder
            }
        }
    }

    DriverScaffoldWrapper(
        title = "Driver Dashboard",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetContainerColor = Black,
            sheetPeekHeight = 200.dp,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Welcome, Driver!",
                        color = Green,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (showReminder) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Set your daily rate to start receiving requests.", color = White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onNavigate(Screen.DriverProfile.route) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Green)
                                    ) {
                                        Text("Set Now", color = Black)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            sharedPrefs.edit().putBoolean("skip_rate_reminder", true).apply()
                                            showReminder = false
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Green)
                                    ) {
                                        Text("Skip Once")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SquareCard("Today's Trips", Icons.Default.DirectionsCar, Modifier.weight(1f)) {}
                        SquareCard(
                            "New Requests",
                            Icons.Default.MarkunreadMailbox,
                            Modifier.weight(1f),
                            onClick = {
                                if (dailyRateSet) {
                                    onNavigate(Screen.DriverNewRequests.route)
                                } else {
                                    scope.launch {
                                        sheetState.snackbarHostState
                                            .showSnackbar("Please set your daily rate to access requests.")
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SquareCard("Approvals", Icons.Default.Schedule, Modifier.weight(1f)) {
                            onNavigate(Screen.ApproveSafaris.route)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            },
            containerColor = Black,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Black)
                ) {
                    DriverMapView()
                }
            }
        )
    }
}

@Composable
fun SquareCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        modifier = modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = Green, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = White, fontSize = 14.sp)
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DriverMapView() {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val geoPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val locationLabel = remember { mutableStateOf("Locating...") }

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // Fetch location
    LaunchedEffect(true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val point = GeoPoint(it.latitude, it.longitude)
                    geoPoint.value = point

                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val result = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (!result.isNullOrEmpty()) {
                            locationLabel.value =
                                result[0].subLocality ?: result[0].locality ?: "Your Area"
                        }
                    } catch (e: Exception) {
                        locationLabel.value = "Unknown Area"
                    }
                }
            }
        }
    }

    if (geoPoint.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Green)
        }
    } else {
        AndroidView(
            factory = { mapView },
            update = {
                geoPoint.value?.let { point ->
                    it.controller.setCenter(point)
                    it.overlays.clear()

                    val marker = Marker(it).apply {
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "You are here: ${locationLabel.value}"
                    }

                    it.overlays.add(marker)
                    it.invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}