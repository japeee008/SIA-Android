package com.example.citucares.features.auth

data class RegisterRequest(
    val studentId: String,
    val fname: String,
    val lname: String,
    val middleInitial: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)