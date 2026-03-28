package com.example.citucares.api

import com.example.citucares.model.LoginRequest
import com.example.citucares.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("auth/v1/token?grant_type=password")
    fun login(
        @Header("apikey") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("auth/v1/signup")
    fun register(
        @Header("apikey") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("rest/v1/users")
    fun insertUser(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("Prefer") prefer: String,   // ✅ ADD THIS
        @Body body: Map<String, String>
    ): Call<Void>
}