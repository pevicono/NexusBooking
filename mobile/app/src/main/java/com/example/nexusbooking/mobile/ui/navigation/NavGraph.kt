package com.example.nexusbooking.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nexusbooking.mobile.ui.home.HomeScreen
import com.example.nexusbooking.mobile.ui.login.LoginScreen
import com.example.nexusbooking.mobile.ui.profile.ProfileScreen
import com.example.nexusbooking.mobile.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val HOME_DASHBOARD = "home/dashboard"
    const val HOME_FACILITIES = "home/facilities"
    const val HOME_BOOKINGS = "home/bookings"
    const val HOME_GROUPS = "home/groups"
    const val HOME_ADMIN = "home/admin"
    const val PROFILE = "profile"
}

enum class HomeTab {
    DASHBOARD, FACILITIES, BOOKINGS, GROUPS, ADMIN
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onLoggedIn = {
                    navController.navigate(Routes.HOME_DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME_DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME_DASHBOARD) {
            HomeScreen(
                initialTab = HomeTab.DASHBOARD,
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> {}
                        HomeTab.FACILITIES -> navController.navigate(Routes.HOME_FACILITIES) {
                            popUpTo(Routes.HOME_DASHBOARD)
                        }
                        HomeTab.BOOKINGS -> navController.navigate(Routes.HOME_BOOKINGS) {
                            popUpTo(Routes.HOME_DASHBOARD)
                        }
                        HomeTab.GROUPS -> navController.navigate(Routes.HOME_GROUPS) {
                            popUpTo(Routes.HOME_DASHBOARD)
                        }
                        HomeTab.ADMIN -> navController.navigate(Routes.HOME_ADMIN) {
                            popUpTo(Routes.HOME_DASHBOARD)
                        }
                    }
                },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.HOME_FACILITIES) {
            HomeScreen(
                initialTab = HomeTab.FACILITIES,
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> navController.navigate(Routes.HOME_DASHBOARD) {
                            popUpTo(Routes.HOME_FACILITIES)
                        }
                        HomeTab.FACILITIES -> {}
                        HomeTab.BOOKINGS -> navController.navigate(Routes.HOME_BOOKINGS) {
                            popUpTo(Routes.HOME_FACILITIES)
                        }
                        HomeTab.GROUPS -> navController.navigate(Routes.HOME_GROUPS) {
                            popUpTo(Routes.HOME_FACILITIES)
                        }
                        HomeTab.ADMIN -> navController.navigate(Routes.HOME_ADMIN) {
                            popUpTo(Routes.HOME_FACILITIES)
                        }
                    }
                },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.HOME_BOOKINGS) {
            HomeScreen(
                initialTab = HomeTab.BOOKINGS,
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> navController.navigate(Routes.HOME_DASHBOARD) {
                            popUpTo(Routes.HOME_BOOKINGS)
                        }
                        HomeTab.FACILITIES -> navController.navigate(Routes.HOME_FACILITIES) {
                            popUpTo(Routes.HOME_BOOKINGS)
                        }
                        HomeTab.BOOKINGS -> {}
                        HomeTab.GROUPS -> navController.navigate(Routes.HOME_GROUPS) {
                            popUpTo(Routes.HOME_BOOKINGS)
                        }
                        HomeTab.ADMIN -> navController.navigate(Routes.HOME_ADMIN) {
                            popUpTo(Routes.HOME_BOOKINGS)
                        }
                    }
                },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.HOME_GROUPS) {
            HomeScreen(
                initialTab = HomeTab.GROUPS,
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> navController.navigate(Routes.HOME_DASHBOARD) {
                            popUpTo(Routes.HOME_GROUPS)
                        }
                        HomeTab.FACILITIES -> navController.navigate(Routes.HOME_FACILITIES) {
                            popUpTo(Routes.HOME_GROUPS)
                        }
                        HomeTab.BOOKINGS -> navController.navigate(Routes.HOME_BOOKINGS) {
                            popUpTo(Routes.HOME_GROUPS)
                        }
                        HomeTab.GROUPS -> {}
                        HomeTab.ADMIN -> navController.navigate(Routes.HOME_ADMIN) {
                            popUpTo(Routes.HOME_GROUPS)
                        }
                    }
                },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.HOME_ADMIN) {
            HomeScreen(
                initialTab = HomeTab.ADMIN,
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> navController.navigate(Routes.HOME_DASHBOARD) {
                            popUpTo(Routes.HOME_ADMIN)
                        }
                        HomeTab.FACILITIES -> navController.navigate(Routes.HOME_FACILITIES) {
                            popUpTo(Routes.HOME_ADMIN)
                        }
                        HomeTab.BOOKINGS -> navController.navigate(Routes.HOME_BOOKINGS) {
                            popUpTo(Routes.HOME_ADMIN)
                        }
                        HomeTab.GROUPS -> navController.navigate(Routes.HOME_GROUPS) {
                            popUpTo(Routes.HOME_ADMIN)
                        }
                        HomeTab.ADMIN -> {}
                    }
                },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME_FACILITIES) { inclusive = true }
                    }
                },
                onNavigateToTab = { tab ->
                    when (tab) {
                        HomeTab.DASHBOARD -> navController.navigate(Routes.HOME_DASHBOARD) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                        HomeTab.FACILITIES -> navController.navigate(Routes.HOME_FACILITIES) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                        HomeTab.BOOKINGS -> navController.navigate(Routes.HOME_BOOKINGS) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                        HomeTab.GROUPS -> navController.navigate(Routes.HOME_GROUPS) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                        HomeTab.ADMIN -> navController.navigate(Routes.HOME_ADMIN) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
