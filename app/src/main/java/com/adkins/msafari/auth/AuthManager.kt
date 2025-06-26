package com.adkins.msafari.auth

import android.annotation.SuppressLint
import android.content.Context
import com.adkins.msafari.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser

    @SuppressLint("StaticFieldLeak")
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(
        email: String,
        password: String,
        role: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    val userData = mapOf(
                        "email" to email,
                        "role" to role,
                        "name" to name
                    )
                    firestore.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Failed to save user data.")
                        }
                } else {
                    onFailure("User ID is null.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Sign-up failed.")
            }
    }

    fun signIn(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val name = doc.getString("name") ?: email
                            val profileImage = doc.getString("profileImageUrl") ?: ""
                            val user = User(
                                uid = uid,
                                name = name,
                                email = email,
                                profileImageUrl = profileImage
                            )
                            AccountManager.init(context)
                            AccountManager.saveAccount(user)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Failed to load user data.")
                        }
                } else {
                    onFailure("User not found.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Login failed.")
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun fetchUserRole(
        onRoleFetched: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure("User not logged in.")
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                if (role != null) {
                    onRoleFetched(role)
                } else {
                    onFailure("Role not found.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to fetch role.")
            }
    }

    fun fetchUserName(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onError("User not logged in.")
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: document.getString("email") ?: "Client"
                onResult(name)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch user name.")
            }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to send reset email.")
            }
    }

    fun logout() {
        auth.signOut()
    }
}