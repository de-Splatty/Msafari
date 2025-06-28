package com.adkins.msafari.models

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val profileImageUrl: String = "" // Optional, can load from Firebase later
)