package com.adkins.msafari.state

data class DriverState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val idNumber: String = "",
    val vehicleType: String = "",
    val plateNumber: String = "",
    val seater: Int = 0,
    val dailyRate: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)