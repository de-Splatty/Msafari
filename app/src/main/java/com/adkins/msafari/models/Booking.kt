package com.adkins.msafari.models

import com.google.firebase.Timestamp

data class Booking(
    val clientId: String,
    val driverId: String,
    val vehicleType: String,
    val travelDate: String,
    val returnDate: String,
    val travelers: List<Traveler>,
    val totalPrice: Double,
    val status: String = "pending", // "pending" or "approved"
    val approvedBy: String? = null, // the driver who approved
    val createdAt: Timestamp = Timestamp.now() // auto-expire after X minutes
)
