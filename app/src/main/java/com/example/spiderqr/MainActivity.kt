package com.example.spiderqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spiderqr.data.UserPreferences
import com.example.spiderqr.screens.MainQRScreen
import com.example.spiderqr.screens.SetupScreen
import com.example.spiderqr.screens.SettingsScreen
import com.example.spiderqr.screens.WelcomeScreen
import com.example.spiderqr.ui.theme.SpiderQRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpiderQRTheme {
                QAreApp()
            }
        }
        36+3
    }
}

@Composable
fun QAreApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToMain = { navController.navigate("main") },
                onNavigateToSetup = { navController.navigate("setup") }
            )
        }
        
        composable("setup") {
            SetupScreen(
                onSetupComplete = { 
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = false }
                    }
                }
            )
        }
        
        composable("main") {
            MainQRScreen(
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onResetQR = { 
                    navController.navigate("setup") {
                        popUpTo("welcome") { inclusive = false }
                    }
                }
            )
        }
    }
}
