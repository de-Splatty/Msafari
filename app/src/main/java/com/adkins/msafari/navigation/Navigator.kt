package com.adkins.msafari.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.adkins.msafari.ui.theme.screens.*
import com.adkins.msafari.viewmodels.BookingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MsafariNavGraph(
    navController: NavHostController,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: Screen.Login.route

    val onNavigate: (String) -> Unit = { route ->
        if (route != currentRoute) {
            navController.navigate(route) {
                popUpTo(Screen.ClientHome.route)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    when (role.lowercase()) {
                        "client" -> navController.navigate(Screen.ClientHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        "driver" -> navController.navigate(Screen.DriverDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        "incomplete_driver" -> navController.navigate(Screen.DriverProfile.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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
                onSwitchToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }

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
                        navController.navigate(Screen.ClientTripStatus.route)
                    },
                    onBookingFailure = { /* handle if needed */ },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.ClientTripStatus.route) {
            ClientTripStatusScreen(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }

        composable(Screen.DriverDashboard.route) {
            DriverDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DriverDashboard.route) { inclusive = true }
                    }
                },

            )
        }

        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }

        composable(Screen.Settings.route) {
            ClientSettingsScreen(
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ClientHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DriverProfile.route) {
            DriverProfileCompletionScreen(
                onSubmit = {
                    navController.navigate(Screen.DriverDashboard.route) {
                        popUpTo(Screen.DriverProfile.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }

        composable(Screen.PendingSafaris.route) {
            PendingSafarisScreen(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    }
}