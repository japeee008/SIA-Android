package com.example.citucares.core.network

import com.example.citucares.features.auth.LoginRequest
import com.example.citucares.features.auth.LoginResponse
import com.example.citucares.features.auth.RegisterRequest
import com.example.citucares.features.chat.ChatHistoryMessage
import com.example.citucares.features.chat.ChatRequest
import com.example.citucares.features.chat.ChatResponse
import com.example.citucares.features.chat.ChatSession
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("auth/register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<String>

    @POST("chat")
    fun sendMessage(
        @Body request: ChatRequest
    ): Call<ChatResponse>

    @GET("chat/sessions")
    fun getChatSessions(
        @Query("userId") userId: Long
    ): Call<List<ChatSession>>

    @GET("chat/history")
    fun getChatHistory(
        @Query("userId") userId: Long,
        @Query("sessionId") sessionId: Long
    ): Call<List<ChatHistoryMessage>>
}