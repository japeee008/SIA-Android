package com.example.citucares.features.settings

data class ChangePasswordRequest(
    val userId: Long,
    val currentPassword: String,
    val newPassword: String
)