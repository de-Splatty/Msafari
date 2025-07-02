package com.adkins.msafari.firestore

import com.adkins.msafari.models.User
import com.google.firebase.firestore.FirebaseFirestore

object UserManager {
    private val db = FirebaseFirestore.getInstance()

    fun saveUserToFirestore(user: User, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}