package com.example.citucares.core.network

import com.example.citucares.features.auth.LoginRequest
import com.example.citucares.features.auth.LoginResponse
import com.example.citucares.features.auth.RegisterRequest
import com.example.citucares.features.chat.ChatHistoryMessage
import com.example.citucares.features.chat.ChatRequest
import com.example.citucares.features.chat.ChatResponse
import com.example.citucares.features.chat.ChatSession
import com.example.citucares.features.settings.UpdateProfileRequest
import com.example.citucares.features.settings.UpdateProfileResponse
import com.example.citucares.features.settings.ChangePasswordRequest
import com.example.citucares.features.auth.ForgotPasswordRequest
import com.example.citucares.features.auth.ForgotPasswordResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @PUT("users/{id}")
    fun updateUserProfile(
        @Path("id") userId: Long,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

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

    @POST("auth/change-password")
    fun changePassword(
        @Body request: ChangePasswordRequest
    ): Call<String>

    @POST("auth/forgot-password")
    fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Call<ForgotPasswordResponse>
}