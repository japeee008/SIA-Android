package com.example.citucares.core.network

import com.example.citucares.core.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    // Emulator
    private const val BASE_URL = "https://citucare-backend.onrender.com/api/"

    // Real phone example:
    // private const val BASE_URL = "http://192.168.1.5:8080/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Scalars first: handles plain String responses from Spring Boot
            .addConverterFactory(ScalarsConverterFactory.create())
            // Gson second: handles JSON objects like LoginResponse
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}