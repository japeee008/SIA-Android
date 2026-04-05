package com.example.citucares.model

data class RegisterRequest(
    val studentId: String,
    val fname: String,
    val lname: String,
    val middleInitial: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)