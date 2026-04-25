package com.example.citucares.features.chat

data class ChatRequest(
    val message: String,
    val sessionId: Long? = null
)