package com.adkins.msafari.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ClientHome : Screen("client_home")
    object DriverDashboard : Screen("driver_dashboard")
    object Booking : Screen("booking")
    object BookingHistory : Screen("booking_history")
    object Settings : Screen("settings")
    object DriverProfile : Screen("driver_profile")
    object BookingConfirmation : Screen("booking_confirmation")



    object TravelerDetails : Screen("travelerDetails/{number}/{children}") {
        fun createRoute(number: Int, children: Int) = "travelerDetails/$number/$children"
    }


    object AvailableDrivers : Screen("available_drivers/{travelDate}/{returnDate}") {
        fun createRoute(travelDate: String, returnDate: String) =
            "available_drivers/$travelDate/$returnDate"
    }



    // Add more screens like BookingConfirmation if needed
}
