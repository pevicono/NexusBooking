package com.example.nexusbooking.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nexusbooking.mobile.ui.admin.AdminScreen
import com.example.nexusbooking.mobile.ui.home.HomeScreen
import com.example.nexusbooking.mobile.ui.login.LoginScreen
import com.example.nexusbooking.mobile.ui.profile.ProfileScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenAdmin = { navController.navigate(Routes.ADMIN) }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.ADMIN) {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}
