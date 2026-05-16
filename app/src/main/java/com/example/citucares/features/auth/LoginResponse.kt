package com.example.citucares.features.auth

data class LoginResponse(
    val userId: Long,
    val email: String,
    val fname: String,
    val lname: String,
    val role: String,
    val institutionalId: String?
)