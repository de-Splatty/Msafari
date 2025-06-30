package com.adkins.msafari.ui.theme.client_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adkins.msafari.R
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.firestore.DriverManager
import com.adkins.msafari.models.User
import com.adkins.msafari.ui.theme.Black
import com.adkins.msafari.ui.theme.Green
import com.adkins.msafari.ui.theme.White
import com.adkins.msafari.utils.ErrorLogger

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onSwitchToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle password visibility", tint = White)
                    }
                },
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
                        context,
                        onSuccess = {
                            AuthManager.fetchUserRole(
                                onRoleFetched = { role ->
                                    val uid = AuthManager.getCurrentUserId()
                                    if (uid != null) {
                                        AuthManager.fetchUserName(
                                            onResult = { name ->
                                                val user = User(
                                                    uid = uid,
                                                    name = name,
                                                    email = email
                                                )
                                                AccountManager.saveAccount(user)
                                            },
                                            onError = {
                                                ErrorLogger.logError(
                                                    "Failed to fetch username after login",
                                                    Exception(it),
                                                    uid
                                                )
                                            }
                                        )

                                        // ✅ Redirect directly to driver dashboard
                                        if (role.lowercase() == "driver") {
                                            onLoginSuccess("driver")
                                        } else {
                                            onLoginSuccess(role)
                                        }
                                    } else {
                                        error = "Failed to get user ID"
                                        ErrorLogger.logError("Login succeeded but UID was null", null)
                                    }
                                },
                                onFailure = {
                                    error = it
                                    ErrorLogger.logError("Failed to fetch user role", Exception(it))
                                }
                            )
                        },
                        onFailure = {
                            error = it
                            ErrorLogger.logError("Login failed", Exception(it), email)
                        }
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