package com.adkins.msafari.ui.theme.driver_screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.navigation.Screen
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SquareCard("Today's Trips", Icons.Default.DirectionsCar, Modifier.weight(1f)) {
                            // Optional: Add logic later
                        }
                        SquareCard("New Requests", Icons.Default.MarkunreadMailbox, Modifier.weight(1f)) {
                            onNavigate(Screen.DriverNewRequests.route)
                        }
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

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun DriverMapView() {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                val startPoint = GeoPoint(-1.2921, 36.8219)
                controller.setCenter(startPoint)

                val marker = Marker(this)
                marker.position = startPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "You are here"
                overlays.add(marker)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}