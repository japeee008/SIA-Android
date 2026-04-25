package com.example.citucares.core

import com.example.citucares.features.auth.LoginRequest
import com.example.citucares.features.auth.RegisterRequest
import com.example.citucares.features.auth.LoginResponse
import com.example.citucares.features.chat.ChatRequest
import com.example.citucares.features.chat.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

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
}