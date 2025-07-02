package com.adkins.msafari.firestore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.adkins.msafari.models.Booking
import com.adkins.msafari.models.BookingData
import com.adkins.msafari.models.Traveler
import com.adkins.msafari.utils.ErrorLogger
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.temporal.ChronoUnit

object BookingManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveBookingToSelectedDrivers(
        clientId: String,
        selectedDrivers: List<Driver>,
        bookingData: BookingData,
        totalCost: Int,
        commission: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        travelers: List<Traveler>
    ) {
        val bookingId = db.collection("bookings").document().id

        // ✅ Fix: Assign first selected driver's ID to booking.driverId
        val primaryDriverId = selectedDrivers.firstOrNull()?.id.orEmpty()

        val booking = Booking(
            clientId = clientId,
            driverId = primaryDriverId,
            vehicleType = bookingData.vehicleType,
            travelDate = bookingData.travelDate,
            returnDate = bookingData.returnDate,
            travelers = bookingData.travelers,
            totalPrice = totalCost.toDouble(),
            status = "pending",
            approvedBy = null,
            pickupLocation = bookingData.pickupLocation,
            destinationLocation = bookingData.destinationLocation,
            createdAt = Timestamp.now()
        )

        val bookingRef = db.collection("bookings").document(bookingId)

        bookingRef.set(booking)
            .addOnSuccessListener {
                val travelersCollection = bookingRef.collection("travelers")
                val travelerTasks = bookingData.travelers.map { traveler ->
                    travelersCollection.add(
                        mapOf(
                            "name" to traveler.name,
                            "age" to traveler.age,
                            "idNumber" to traveler.idNumber
                        )
                    )
                }

                com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(travelerTasks)
                    .addOnSuccessListener {
                        val requestTasks = selectedDrivers.map { driver ->
                            db.collection("drivers")
                                .document(driver.id)
                                .collection("booking_requests")
                                .document(bookingId)
                                .set(
                                    mapOf(
                                        "bookingId" to bookingId,
                                        "status" to "pending",
                                        "clientId" to clientId,
                                        "travelDate" to bookingData.travelDate,
                                        "returnDate" to bookingData.returnDate,
                                        "timestamp" to System.currentTimeMillis()
                                    )
                                )
                        }

                        com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(requestTasks)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e ->
                                ErrorLogger.logError("Failed to send booking to drivers", e, auth.currentUser?.uid)
                                onFailure(e.message ?: "Failed to send booking to drivers.")
                            }
                    }
                    .addOnFailureListener { e ->
                        ErrorLogger.logError("Failed to save travelers", e, auth.currentUser?.uid)
                        onFailure(e.message ?: "Failed to save travelers.")
                    }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to save booking", e, auth.currentUser?.uid)
                onFailure(e.message ?: "Failed to save booking.")
            }
    }

    fun approveBooking(
        bookingId: String,
        driverId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val bookingRef = db.collection("bookings").document(bookingId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(bookingRef)
            val currentStatus = snapshot.getString("status")
            val currentApprovedBy = snapshot.getString("approvedBy")

            if (currentStatus == "pending" && currentApprovedBy == null) {
                transaction.update(bookingRef, mapOf(
                    "status" to "approved",
                    "approvedBy" to driverId
                ))

                db.collection("drivers")
                    .get()
                    .addOnSuccessListener { result ->
                        result.documents.forEach { doc ->
                            val dId = doc.id
                            if (dId != driverId) {
                                db.collection("drivers")
                                    .document(dId)
                                    .collection("booking_requests")
                                    .document(bookingId)
                                    .delete()
                            }
                        }
                    }
            }
        }.addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to approve booking", e, auth.currentUser?.uid)
                onFailure(e.message ?: "Failed to approve booking.")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autoExpireOldBookings(
        expiryMinutes: Long = 5
    ) {
        val cutoff = Instant.now().minus(expiryMinutes, ChronoUnit.MINUTES).epochSecond

        db.collection("bookings")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result.documents) {
                    val timestamp = doc.getTimestamp("createdAt") ?: continue
                    if (timestamp.seconds < cutoff) {
                        db.collection("bookings")
                            .document(doc.id)
                            .update("status", "expired")
                    }
                }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to auto-expire old bookings", e, auth.currentUser?.uid)
            }
    }

    fun getBookingsForUser(
        userId: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("bookings")
            .whereEqualTo("clientId", userId)
            .get()
            .addOnSuccessListener { result ->
                val bookings = result.documents.mapNotNull { it.data }
                onSuccess(bookings)
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to fetch bookings for user", e, userId)
                onFailure(e.message ?: "Failed to fetch bookings.")
            }
    }

    fun fetchLatestBookingForClient(
        clientId: String,
        onSuccess: (Booking) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("bookings")
            .whereEqualTo("clientId", clientId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                if (doc != null) {
                    val booking = doc.toObject(Booking::class.java)
                    if (booking != null) {
                        onSuccess(booking)
                    } else {
                        ErrorLogger.logError("Failed to parse booking", null, clientId)
                        onFailure("Failed to parse booking.")
                    }
                } else {
                    onFailure("No bookings found.")
                }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Error fetching latest booking", e, clientId)
                onFailure(e.message ?: "Error fetching latest booking.")
            }
    }
}