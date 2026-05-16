package com.example.citucares.features.auth

data class ForgotPasswordResponse(
    val message: String,
    val resetToken: String
)