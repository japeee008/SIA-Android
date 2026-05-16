package com.example.citucares.features.chat

data class ChatRequest(
    val message: String,
    val userId: Long,
    val sessionId: Long? = null,
)