package com.adkins.msafari.models

data class User(
    val uid: String,
    val name: String? = null,
    val email: String? = null,
    val role: String
)