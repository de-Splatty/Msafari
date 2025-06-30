package com.adkins.msafari.ui.theme.client_screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.adkins.msafari.R
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.auth.AuthManager
import com.adkins.msafari.components.ClientScaffoldWrapper
import com.adkins.msafari.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    onSwitchAccount: () -> Unit = {},
    onPickImage: (Uri?) -> Unit = {}
) {
    val context = LocalContext.current
    var dropdownExpanded by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Loading...") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val savedAccounts = remember { mutableStateListOf<User>() }

    val colorScheme = MaterialTheme.colorScheme

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        onPickImage(uri)
    }

    LaunchedEffect(Unit) {
        AuthManager.fetchUserName(
            onResult = { username = it },
            onError = { username = "Client" }
        )
        AccountManager.init(context)
        savedAccounts.clear()
        savedAccounts.addAll(AccountManager.getSavedAccounts())
    }

    ClientScaffoldWrapper(
        title = "Profile",
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        showProfile = true,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = profileImageUri?.let { rememberAsyncImagePainter(it) }
                        ?: painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = username,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Button(
                    onClick = { /* Future: Update account */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Let's update your account")
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionList(
                    title = "Account Settings",
                    items = listOf("Personal info", "Safety", "Login & security", "Privacy")
                )

                SectionList(
                    title = "Preferences",
                    items = listOf("Language English - US", "Communication preferences", "Calendars", "Dark mode")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { dropdownExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.onBackground
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Switch Account")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(colorScheme.surface)
                    ) {
                        if (savedAccounts.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Login to Existing Account") },
                                onClick = {
                                    dropdownExpanded = false
                                    onSwitchAccount()
                                }
                            )
                        } else {
                            savedAccounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(account.name) },
                                    onClick = {
                                        dropdownExpanded = false
                                        AuthManager.loginFromAccount(
                                            user = account,
                                            context = context,
                                            onSuccess = { role ->
                                                when (role) {
                                                    "client" -> onNavigate("client_dashboard")
                                                    "driver" -> onNavigate("driver_dashboard")
                                                    "incomplete_driver" -> onNavigate("complete_driver_profile")
                                                    else -> onNavigate("home")
                                                }
                                            },
                                            onFailure = { /* TODO: show toast/snackbar */ }
                                        )
                                    }
                                )
                            }

                            Divider()

                            DropdownMenuItem(
                                text = { Text("Login to Another Account") },
                                onClick = {
                                    dropdownExpanded = false
                                    onSwitchAccount()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { /* TODO: Delete account logic */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete account", color = colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        AuthManager.logout()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.onBackground)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }
    )
}

@Composable
private fun SectionList(title: String, items: List<String>) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, color = colorScheme.primary, fontWeight = FontWeight.SemiBold)
        items.forEach { item ->
            Text(
                text = item,
                fontSize = 16.sp,
                color = colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { /* TODO: Implement navigation or toggle */ }
            )
        }
        Divider(Modifier.padding(vertical = 8.dp), color = colorScheme.outline)
    }
}