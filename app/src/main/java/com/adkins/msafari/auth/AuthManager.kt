package com.adkins.msafari.auth

import android.annotation.SuppressLint
import android.content.Context
import com.adkins.msafari.models.User
import com.adkins.msafari.utils.ErrorLogger
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
                            ErrorLogger.logError("Failed to save user data during signUp", e, uid)
                            onFailure(e.message ?: "Failed to save user data.")
                        }
                } else {
                    ErrorLogger.logError("Sign-up failed: User ID is null")
                    onFailure("User ID is null.")
                }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Sign-up failed for $email", e)
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
                            val name = doc.getString("name")?.takeIf { it.isNotBlank() } ?: email
                            val role = doc.getString("role")?.takeIf { it.isNotBlank() } ?: "client"

                            val user = User(
                                uid = uid,
                                name = name,
                                email = email,
                                role = role
                            )

                            AccountManager.init(context)
                            AccountManager.saveAccount(user)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            ErrorLogger.logError("Failed to load user data during signIn", e, uid)
                            onFailure(e.message ?: "Failed to load user data.")
                        }
                } else {
                    ErrorLogger.logError("Sign-in failed: User not found after signIn for $email")
                    onFailure("User not found.")
                }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Login failed for $email", e)
                onFailure(e.message ?: "Login failed.")
            }
    }

    fun loginFromAccount(
        user: User,
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signOut()

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val updatedUser = user.copy(
                    name = doc.getString("name")?.takeIf { it.isNotBlank() } ?: user.name,
                    role = doc.getString("role")?.takeIf { it.isNotBlank() } ?: "client"
                )
                AccountManager.init(context)
                AccountManager.saveAccount(updatedUser)
                onSuccess(updatedUser.role.lowercase())
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to loginFromAccount for ${user.uid}", e, user.uid)
                onFailure(e.message ?: "Failed to login from account.")
            }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun fetchUserRole(
        onRoleFetched: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            ErrorLogger.logError("fetchUserRole failed: user not logged in")
            onFailure("User not logged in.")
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")?.takeIf { it.isNotBlank() }
                if (role != null) {
                    onRoleFetched(role)
                } else {
                    ErrorLogger.logError("Role not found for $uid", null, uid)
                    onFailure("Role not found.")
                }
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to fetch role for $uid", e, uid)
                onFailure(e.message ?: "Failed to fetch role.")
            }
    }

    fun fetchUserName(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            ErrorLogger.logError("fetchUserName failed: user not logged in")
            onError("User not logged in.")
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")?.takeIf { it.isNotBlank() }
                    ?: document.getString("email") ?: "Client"
                onResult(name)
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to fetch user name for $uid", e, uid)
                onError(e.message ?: "Failed to fetch user name.")
            }
    }

    fun fetchCurrentUserDetails(
        onResult: (User?) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            ErrorLogger.logError("fetchCurrentUserDetails failed: user not logged in")
            onError("User not logged in.")
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val user = User(
                    uid = uid,
                    name = doc.getString("name")?.takeIf { it.isNotBlank() } ?: "Unknown",
                    email = doc.getString("email")?.takeIf { it.isNotBlank() } ?: "No email",
                    role = doc.getString("role")?.takeIf { it.isNotBlank() } ?: "client"
                )
                onResult(user)
            }
            .addOnFailureListener { e ->
                ErrorLogger.logError("Failed to fetch user details for $uid", e, uid)
                onError(e.message ?: "Failed to fetch user details.")
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
                ErrorLogger.logError("Failed to send password reset email to $email", e)
                onFailure(e.message ?: "Failed to send reset email.")
            }
    }

    fun logout() {
        auth.signOut()
    }
}