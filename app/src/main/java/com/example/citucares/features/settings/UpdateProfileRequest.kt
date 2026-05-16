package com.example.citucares.features.settings

data class UpdateProfileRequest(
    val fname: String,
    val lname: String,
    val email: String,
    val role: String
)