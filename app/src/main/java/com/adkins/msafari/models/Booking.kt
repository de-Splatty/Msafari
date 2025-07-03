package com.adkins.msafari.models

import com.google.firebase.Timestamp

data class Booking(
    val id: String = "", // Firebase document ID
    val clientId: String = "",
    val driverId: String = "", // Initially requested driver (or empty if random)
    val vehicleType: String = "",
    val travelDate: String = "",
    val returnDate: String = "",
    val travelers: List<Traveler> = emptyList(),
    val totalPrice: Double = 0.0,
    val dailyRate: Int = 0,
    val pickupLocation: String = "",
    val destinationLocation: String = "",
    val status: String = "pending", // can be "pending", "approved", or "expired"
    val approvedBy: String? = null, // set when a driver accepts the booking
    val createdAt: Timestamp = Timestamp.now()
)