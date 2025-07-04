package com.adkins.msafari.firestore

import com.adkins.msafari.models.User
import com.google.firebase.firestore.FirebaseFirestore

object UserManager {
    private val db = FirebaseFirestore.getInstance()

    fun saveUserToFirestore(
        user: User,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val uid = user.uid
        if (uid.isNullOrBlank()) {
            onFailure(IllegalArgumentException("User UID is null or blank. Cannot save to Firestore."))
            return
        }

        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}