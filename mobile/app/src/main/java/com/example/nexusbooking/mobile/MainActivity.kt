package com.example.nexusbooking.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.nexusbooking.mobile.ui.navigation.NavGraph
import com.example.nexusbooking.mobile.ui.theme.NexusBookingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusBookingTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
