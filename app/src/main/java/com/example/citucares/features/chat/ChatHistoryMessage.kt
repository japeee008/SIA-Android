package com.example.citucares.features.chat

data class ChatHistoryMessage(
    val messageId: Long,
    val sessionId: Long,
    val messageText: String?,
    val botReply: String?,
    val timestamp: String?,
    val categoryName: String?
)