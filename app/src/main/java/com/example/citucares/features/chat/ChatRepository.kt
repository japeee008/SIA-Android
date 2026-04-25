package com.example.citucares.features.chat

import com.example.citucares.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback

class ChatRepository {

    fun sendMessage(
        request: ChatRequest,
        callback: Callback<ChatResponse>
    ) {
        RetrofitClient.instance.sendMessage(request).enqueue(callback)
    }
}