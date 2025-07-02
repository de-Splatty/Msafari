package com.adkins.msafari.firestore

import com.adkins.msafari.utils.ErrorLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Driver(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val seater: Int = 0,
    val phoneNumber: String = "",
    val nationalId: String = ""
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
                ErrorLogger.logError("Error fetching available drivers", e, null)
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
                val phone = doc.getString("phoneNumber")
                val nationalId = doc.getString("nationalId")

                val complete = !name.isNullOrBlank() &&
                        !plate.isNullOrBlank() &&
                        !type.isNullOrBlank() &&
                        seats != null && seats > 0 &&
                        !phone.isNullOrBlank() &&
                        !nationalId.isNullOrBlank()

                onResult(complete)
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to check driver profile completeness", e, driverId)
                onResult(false)
            }
    }

    fun saveDriverProfile(
        name: String,
        plateNumber: String,
        vehicleType: String,
        seater: Int?,
        phoneNumber: String,
        nationalId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            val msg = "User not logged in while trying to save driver profile."
            ErrorLogger.logError(msg, userId = null)
            onFailure(msg)
            return
        }

        val data = mapOf(
            "uid" to uid,
            "name" to name,
            "plateNumber" to plateNumber,
            "vehicleType" to vehicleType,
            "seater" to seater,
            "phoneNumber" to phoneNumber,
            "nationalId" to nationalId,
            "isProfileComplete" to true
        )

        firestore.collection("drivers")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to save driver profile", e, uid)
                onFailure(e.message ?: "Failed to save driver profile.")
            }
    }

    fun getDriverProfile(
        driverId: String,
        onResult: (Driver?) -> Unit
    ) {
        firestore.collection("drivers").document(driverId)
            .get()
            .addOnSuccessListener { doc ->
                val driver = doc.toObject(Driver::class.java)?.copy(id = doc.id)
                onResult(driver)
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to fetch driver profile", e, driverId)
                onResult(null)
            }
    }
}