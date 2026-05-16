package com.example.citucares.features.settings

data class UpdateProfileResponse(
    val userId: Long,
    val institutionalId: String?,
    val fname: String,
    val lname: String,
    val email: String,
    val role: String
)