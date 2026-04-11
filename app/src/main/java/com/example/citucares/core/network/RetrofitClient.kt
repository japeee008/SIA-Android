package com.example.citucares.core.network

import com.example.citucares.core.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulator
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Real phone example:
    // private const val BASE_URL = "http://192.168.1.5:8080/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}