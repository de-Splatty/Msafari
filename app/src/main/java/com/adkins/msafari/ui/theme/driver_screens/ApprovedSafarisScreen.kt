package com.adkins.msafari.ui.theme.driver_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.components.DriverScaffoldWrapper
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Safari(
    val id: String = "",
    val destinationLocation: String = "",
    val pickupLocation: String = "",
    val travelDate: String = "",
    val returnDate: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ApprovedSafarisScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var safaris by remember { mutableStateOf<List<Safari>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = loading,
        onRefresh = {
            scope.launch {
                loading = true
                loadApprovedSafaris(
                    db = db,
                    driverId = auth.currentUser?.uid ?: "",
                    onLoaded = { safaris = it; loading = false }
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        loading = true
        loadApprovedSafaris(
            db = db,
            driverId = auth.currentUser?.uid ?: "",
            onLoaded = { safaris = it; loading = false }
        )
    }

    DriverScaffoldWrapper(
        title = "Approved Safaris",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .pullRefresh(pullRefreshState)
                .padding(innerPadding)
        ) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    text = "Approved Safaris",
                    fontSize = 20.sp,
                    color = Green,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Start)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(safaris) { safari ->
                        ApprovedSafariCard(safari) {
                            onNavigateToDetails(safari.id)
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Green
            )
        }
    }
}

suspend fun loadApprovedSafaris(
    db: FirebaseFirestore,
    driverId: String,
    onLoaded: (List<Safari>) -> Unit
) {
    val result = db.collection("bookings")
        .whereEqualTo("approvedBy", driverId) // ✅ CORRECTED field
        .whereEqualTo("status", "approved")
        .get()
        .await()

    val list = result.documents.map { doc ->
        Safari(
            id = doc.id,
            destinationLocation = doc.getString("destinationLocation") ?: "",
            pickupLocation = doc.getString("pickupLocation") ?: "",
            travelDate = doc.getString("travelDate") ?: "",
            returnDate = doc.getString("returnDate") ?: ""
        )
    }

    onLoaded(list)
}

@Composable
fun ApprovedSafariCard(safari: Safari, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Trip Icon",
                    tint = Green
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    safari.destinationLocation,
                    fontSize = 18.sp,
                    color = White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Pickup: ${safari.pickupLocation}", color = White, fontSize = 14.sp)
            Text("Travel: ${safari.travelDate} to ${safari.returnDate}", color = White, fontSize = 14.sp)
        }
    }
}