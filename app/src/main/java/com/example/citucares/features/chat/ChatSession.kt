package com.example.citucares.features.chat

data class ChatSession(
    val sessionId: Long,
    val title: String,
    val createdAt: String? = null,
    val lastActivityAt: String? = null
)