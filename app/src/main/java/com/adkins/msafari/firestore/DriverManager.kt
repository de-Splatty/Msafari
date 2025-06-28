package com.adkins.msafari.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

data class Driver(
    val id: String = "",
    val name: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val seater: Int = 0,
    val profilePicUrl: String = ""
)

object DriverManager {
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchAvailableDrivers(
        travelDate: String,
        returnDate: String,
        onSuccess: (List<Driver>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("drivers")
            .get()
            .addOnSuccessListener { result ->
                val drivers = result.documents.mapNotNull { doc ->
                    doc.toObject(Driver::class.java)?.copy(id = doc.id)
                }
                onSuccess(drivers)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching drivers.")
            }
    }

    fun isDriverProfileComplete(
        driverId: String,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection("drivers").document(driverId).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name")
                val plate = doc.getString("plateNumber")
                val type = doc.getString("vehicleType")
                val seats = doc.getLong("seater")

                val complete = !name.isNullOrBlank() &&
                        !plate.isNullOrBlank() &&
                        !type.isNullOrBlank() &&
                        seats != null && seats > 0

                onResult(complete)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun saveDriverProfile(
        name: String,
        plateNumber: String,
        vehicleType: String,
        seater: Int,
        profilePicUrl: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = mapOf(
            "name" to name,
            "plateNumber" to plateNumber,
            "vehicleType" to vehicleType,
            "seater" to seater,
            "profilePicUrl" to profilePicUrl,
            "uid" to uid
        )

        firestore.collection("drivers")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save driver profile.") }
    }
}
