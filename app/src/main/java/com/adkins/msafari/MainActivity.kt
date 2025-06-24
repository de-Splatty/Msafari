package com.adkins.msafari

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.adkins.msafari.navigation.MsafariNavGraph
import com.adkins.msafari.ui.theme.MsafariTheme
import com.adkins.msafari.utils.NotificationUtils
import com.adkins.msafari.viewmodels.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel on launch
        NotificationUtils.createNotificationChannel(applicationContext)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkMode by settingsViewModel.darkModeEnabled.collectAsState()

            val navController = rememberNavController()

            MsafariTheme(isDarkTheme = isDarkMode) {
                MsafariNavGraph(navController = navController)
            }
        }
    }
}