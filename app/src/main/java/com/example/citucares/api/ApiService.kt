package com.example.citucares.api

import com.example.citucares.model.LoginRequest
import com.example.citucares.model.RegisterRequest
import com.example.citucares.model.LoginResponse
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
}