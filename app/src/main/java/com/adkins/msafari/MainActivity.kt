package com.adkins.msafari

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.adkins.msafari.auth.AccountManager
import com.adkins.msafari.navigation.MsafariNavGraph
import com.adkins.msafari.ui.theme.MsafariTheme
import com.adkins.msafari.utils.NotificationUtils
import com.adkins.msafari.viewmodels.BookingViewModel
import com.adkins.msafari.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize AccountManager to prevent crash
        AccountManager.init(applicationContext)

        // 📣 Create notification channel (for geofencing alerts)
        NotificationUtils.createNotificationChannel(applicationContext)

        setContent {
            // 🌙 Handle theme switching
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkMode by settingsViewModel.darkModeEnabled.collectAsState()

            // 🌍 Navigation controller
            val navController = rememberNavController()

            // 📦 Booking view model for managing booking-related data
            val bookingViewModel: BookingViewModel = viewModel()

            // 🎨 App theme wrapper
            MsafariTheme(isDarkTheme = isDarkMode) {
                MsafariNavGraph(
                    navController = navController,
                    bookingViewModel = bookingViewModel
                )
            }
        }
    }
}