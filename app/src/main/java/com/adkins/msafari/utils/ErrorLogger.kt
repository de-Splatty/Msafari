package com.adkins.msafari.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object ErrorLogger {
    private const val TAG = "MsafariError"
    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun logError(message: String, exception: Exception? = null, userId: String? = null) {
        Log.e(TAG, message, exception)

        // Crashlytics Logging
        crashlytics.log(message)
        exception?.let { crashlytics.recordException(it) }
        userId?.let { crashlytics.setUserId(it) }

        // Firestore Logging (non-blocking)
        logToFirestore(message, exception, userId)
    }

    private fun logToFirestore(message: String, exception: Exception?, userId: String?) {
        val logEntry = mapOf(
            "message" to message,
            "userId" to (userId ?: "unknown"),
            "exception" to (exception?.localizedMessage ?: "none"),
            "timestamp" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        firestore.collection("logs")
            .add(logEntry)
            .addOnFailureListener {
                Log.e(TAG, "Failed to log error to Firestore", it)
            }
    }
}