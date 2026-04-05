package com.example.citucares.model

data class LoginResponse(
    val userId: Long,
    val email: String,
    val fname: String,
    val lname: String,
    val role: String
)