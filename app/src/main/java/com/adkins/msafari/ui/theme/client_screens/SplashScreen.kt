package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val isLoggedIn = remember { AuthManager.currentUser != null }

    LaunchedEffect(Unit) {
        delay(2000) // 2 second splash delay

        if (isLoggedIn) {
            AuthManager.fetchUserRole(
                onRoleFetched = { role ->
                    val lowerRole = role.lowercase()
                    val uid = AuthManager.getCurrentUserId()

                    when (lowerRole) {
                        "client" -> navController.navigate(Screen.ClientHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }

                        "driver" -> {
                            if (uid != null) {
                                DriverManager.isDriverProfileComplete(uid) { isComplete ->
                                    if (isComplete) {
                                        navController.navigate(Screen.DriverDashboard.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(Screen.DriverProfile.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            }
                        }

                        else -> navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SplashScreen.route) { inclusive = true }
                        }
                    }
                },
                onFailure = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Msafari",
                fontSize = 32.sp,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}