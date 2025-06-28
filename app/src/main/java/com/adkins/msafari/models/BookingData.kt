package com.adkins.msafari.models

data class BookingData(
    val travelDate: String,
    val returnDate: String,
    val vehicleType: String,
    val hasChildren: Boolean,
    val numberOfTravelers: Int,
    val numberOfChildren: Int,
    val pickupLocation: String = "",
    val destinationLocation: String = "",
    val travelers: List<Traveler> = emptyList()
)