package com.adkins.msafari

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.adkins.msafari.navigation.MsafariNavGraph
import com.adkins.msafari.ui.theme.MsafariTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MsafariTheme {
                val navController = rememberNavController()
                MsafariNavGraph(navController = rememberNavController())
            }
        }
        }
    }

