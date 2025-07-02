package com.adkins.msafari.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.adkins.msafari.ui.theme.client_screens.*
import com.adkins.msafari.ui.theme.driver_screens.*
import com.adkins.msafari.viewmodels.BookingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MsafariNavGraph(
    navController: NavHostController,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Login.route

    val onNavigate: (String) -> Unit = { route ->
        if (route != currentRoute) {
            navController.navigate(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { route ->
                    when (route.lowercase()) {
                        "client_home" -> navController.navigate(Screen.ClientHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        "driver_dashboard" -> navController.navigate(Screen.DriverDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        "driver_info" -> navController.navigate(Screen.DriverInfo.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        else -> navController.navigate(Screen.Login.route)
                    }
                },
                onSwitchToSignup = { navController.navigate(Screen.Signup.route) }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onSwitchToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        // Client Screens
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(
                onStartBooking = { navController.navigate(Screen.Booking.route) },
                onViewHistory = { navController.navigate(Screen.BookingHistory.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }

        composable(Screen.Booking.route) {
            BookingScreen(
                onContinue = { bookingData ->
                    bookingViewModel.setBooking(bookingData)
                    navController.navigate(
                        Screen.TravelerDetails.createRoute(
                            bookingData.numberOfTravelers,
                            bookingData.numberOfChildren
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.TravelerDetails.route,
            arguments = listOf(
                navArgument("number") { type = NavType.IntType },
                navArgument("children") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number") ?: 1
            val children = backStackEntry.arguments?.getInt("children") ?: 0
            val hasChildren = bookingViewModel.bookingData.value?.hasChildren ?: false

            TravelerDetailsScreen(
                numberOfTravelers = number,
                hasChildren = hasChildren,
                numberOfChildren = children,
                onSubmit = { travelers ->
                    bookingViewModel.setTravelers(travelers)
                    val travelDate = bookingViewModel.bookingData.value?.travelDate ?: ""
                    val returnDate = bookingViewModel.bookingData.value?.returnDate ?: ""
                    navController.navigate(
                        Screen.AvailableDrivers.createRoute(travelDate, returnDate)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AvailableDrivers.route,
            arguments = listOf(
                navArgument("travelDate") { type = NavType.StringType },
                navArgument("returnDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val travelDate = backStackEntry.arguments?.getString("travelDate") ?: ""
            val returnDate = backStackEntry.arguments?.getString("returnDate") ?: ""
            val travelers = bookingViewModel.travelerList.value

            AvailableDriversScreen(
                travelDate = travelDate,
                returnDate = returnDate,
                travelers = travelers,
                onContinue = { selectedDrivers ->
                    bookingViewModel.setSelectedDrivers(selectedDrivers)
                    navController.navigate(Screen.BookingConfirmation.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BookingConfirmation.route) {
            val bookingData = bookingViewModel.bookingData.value
            val travelers = bookingViewModel.travelerList.value

            if (bookingData != null && travelers.isNotEmpty()) {
                BookingConfirmationScreen(
                    bookingViewModel = bookingViewModel,
                    bookingData = bookingData,
                    selectedDrivers = bookingViewModel.selectedDrivers,
                    travelers = travelers,
                    onBookingSuccess = {
                        navController.navigate(Screen.ClientTripStatus.route) {
                            popUpTo(Screen.ClientHome.route)
                        }
                    },
                    onBookingFailure = {},
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.ClientTripStatus.route) {
            ClientTripStatusScreen(currentRoute, onNavigate)
        }

        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(currentRoute, onNavigate)
        }

        composable(Screen.Settings.route) {
            ClientSettingsScreen(
                currentRoute,
                onNavigate,
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ClientHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                currentRoute,
                onNavigate,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onSwitchAccount = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PendingSafaris.route) {
            PendingSafarisScreen(currentRoute, onNavigate)
        }

        // Driver Screens
        composable(Screen.DriverDashboard.route) {
            DriverDashboardScreen(
                currentRoute,
                onNavigate,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DriverDashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DriverInfo.route) {
            DriverInfoScreen(currentRoute, onNavigate)
        }

        composable(Screen.DriverProfile.route) {
            DriverProfileScreen(
                currentRoute,
                onNavigate,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DriverProfile.route) { inclusive = true }
                    }
                },
                onSwitchAccount = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DriverProfile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DriverSettings.route) {
            DriverSettingsScreen(
                currentRoute,
                onNavigate,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DriverSettings.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DriverPayments.route) {
            DriverPaymentsScreen(currentRoute, onNavigate)
        }

        composable(Screen.PastDrives.route) {
            PastDrivesScreen(currentRoute, onNavigate)
        }

        composable(Screen.DriverAlerts.route) {
            DriverAlertsScreen(currentRoute, onNavigate)
        }

        composable(Screen.ApproveSafaris.route) {
            ApprovedSafarisScreen(
                currentRoute,
                onNavigate,
                onNavigateToDetails = { bookingId ->
                    navController.navigate(Screen.DriverRequestDetails.createRoute(bookingId))
                }
            )
        }

        composable(
            route = Screen.DriverRequestDetails.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            DriverRequestDetailsScreen(
                bookingId = bookingId,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DriverNewRequests.route) {
            DriverNewRequestsScreen(
                currentRoute,
                onNavigate,
                onNavigateToDetails = { bookingId ->
                    navController.navigate(Screen.DriverRequestDetails.createRoute(bookingId))
                }
            )
        }
    }
}