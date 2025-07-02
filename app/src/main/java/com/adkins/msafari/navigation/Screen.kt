package com.adkins.msafari.navigation

sealed class Screen(val route: String) {

    // Auth
    object Login : Screen("login")
    object Signup : Screen("signup")

    // Splash
    object SplashScreen : Screen("splash_screen")

    // Client Flow
    object ClientHome : Screen("client_home")
    object Booking : Screen("booking")
    object BookingHistory : Screen("booking_history")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object PendingSafaris : Screen("pending_safaris")
    object ClientTripStatus : Screen("client_trip_status")

    object TravelerDetails : Screen("travelerDetails/{number}/{children}") {
        fun createRoute(number: Int, children: Int) = "travelerDetails/$number/$children"
    }

    object AvailableDrivers : Screen("available_drivers/{travelDate}/{returnDate}") {
        fun createRoute(travelDate: String, returnDate: String) =
            "available_drivers/$travelDate/$returnDate"
    }

    object BookingConfirmation : Screen("booking_confirmation")

    // Driver Flow
    object DriverDashboard : Screen("driver_dashboard")
    object DriverProfile : Screen("driver_profile")
    object DriverSettings : Screen("driver_settings")
    object DriverPayments : Screen("driver_payments")
    object PastDrives : Screen("past_drives")
    object DriverAlerts : Screen("driver_alerts")
    object ApproveSafaris : Screen("approve_safaris")
    object DriverNewRequests : Screen("driver_new_requests")

    object DriverRequestDetails : Screen("driver_request_details/{bookingId}") {
        fun createRoute(bookingId: String) = "driver_request_details/$bookingId"
    }

    object DriverInfo : Screen("driver_info")

}