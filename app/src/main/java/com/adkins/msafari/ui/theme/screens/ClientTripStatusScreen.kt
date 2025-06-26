package com.adkins.msafari.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.ClientScaffoldWrapper
import com.adkins.msafari.firestore.BookingManager
import com.adkins.msafari.models.Booking
import com.adkins.msafari.navigation.Screen
import com.google.android.gms.location.*
import kotlinx.coroutines.awaitCancellation
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientTripStatusScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var clientMarker: Marker? by remember { mutableStateOf(null) }
    var destinationMarker: Marker? by remember { mutableStateOf(null) }
    var routeLine: Polyline? by remember { mutableStateOf(null) }
    var destinationPoint by remember { mutableStateOf<GeoPoint?>(null) }

    val hasPermission = remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    // Fetch booking
    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            val userId = AuthManager.getCurrentUserId()
            if (userId != null) {
                BookingManager.fetchLatestBookingForClient(
                    clientId = userId,
                    onSuccess = {
                        booking = it
                        isLoading = false

                        val locationParts = it.destinationLocation?.split(",")?.map { part -> part.trim() }
                        if (locationParts?.size == 2) {
                            val lat = locationParts[0].toDoubleOrNull()
                            val lng = locationParts[1].toDoubleOrNull()
                            if (lat != null && lng != null) {
                                val destination = GeoPoint(lat, lng)
                                destinationPoint = destination

                                mapView?.let { map ->
                                    destinationMarker = Marker(map).apply {
                                        position = destination
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = "Destination"
                                        map.overlays.add(this)
                                        map.invalidate()
                                    }
                                }
                            }
                        }
                    },
                    onFailure = {
                        isLoading = false
                        Toast.makeText(context, "Failed to load booking: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Real-time location updates
    LaunchedEffect(hasPermission.value) {
        if (hasPermission.value) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    val geoPoint = GeoPoint(location.latitude, location.longitude)

                    mapView?.let { map ->
                        map.controller.animateTo(geoPoint)

                        if (clientMarker == null) {
                            clientMarker = Marker(map).apply {
                                position = geoPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "You"
                                map.overlays.add(this)
                            }
                        } else {
                            clientMarker?.position = geoPoint
                        }

                        destinationPoint?.let { dest ->
                            val polyline = Polyline().apply {
                                addPoint(geoPoint)
                                addPoint(dest)
                            }
                            routeLine?.let { map.overlays.remove(it) }
                            routeLine = polyline
                            map.overlays.add(polyline)
                        }

                        map.invalidate()
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, callback, null)

            try {
                awaitCancellation()
            } finally {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }
    }

    // MapView lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ✅ Wrapped UI using your ClientScaffoldWrapper
    ClientScaffoldWrapper(
        title = "Trip Status",
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        showTripStatus = true,
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AndroidView(
                        factory = {
                            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)
                                mapView = this
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    booking?.let {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Vehicle: ${it.vehicleType}")
                            Text("Status: ${it.status}")
                            Text("Travel Date: ${it.travelDate}")
                            Text("Return Date: ${it.returnDate}")
                            Text("Travelers: ${it.travelers.size}")
                            Text("Destination: ${it.destinationLocation}")
                            Text("Approved Driver: ${it.approvedBy ?: "Awaiting Approval"}")
                        }
                    } ?: Text("No active trip found.")
                }
            }
        },
        true
    )
}