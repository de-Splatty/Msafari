package com.adkins.msafari.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.R
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onSwitchToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.lion),
            contentDescription = "Lion Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.30f
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text("Login to Msafari", color = White, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = White) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = White) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    AuthManager.signIn(
                        email,
                        password,
                        onSuccess = {
                            AuthManager.fetchUserRole(
                                onRoleFetched = { role ->
                                    if (role.lowercase() == "driver") {
                                        val currentDriverId = AuthManager.getCurrentUserId()
                                        if (currentDriverId != null) {
                                            DriverManager.isDriverProfileComplete(currentDriverId) { isComplete ->
                                                if (isComplete) {
                                                    onLoginSuccess("driver")
                                                } else {
                                                    onLoginSuccess("incomplete_driver")
                                                }
                                            }
                                        } else {
                                            error = "Failed to get driver ID"
                                        }
                                    } else {
                                        onLoginSuccess(role)
                                    }
                                },
                                onFailure = { error = it }
                            )
                        },
                        onFailure = { error = it }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text("Login", color = Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onSwitchToSignup) {
                Text("Don't have an account? Sign Up", color = White)
            }
        }
    }
}

@Composable
fun textFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Green,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = Green,
    cursorColor = Green,
    unfocusedLabelColor = White,
    focusedTextColor = White,
    unfocusedTextColor = White
)