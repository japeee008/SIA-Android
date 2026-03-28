package com.example.citucares.model

data class LoginResponse(
    val access_token: String?,
    val refresh_token: String?,
    val user: User?
)

data class User(
    val id: String,
    val email: String
)