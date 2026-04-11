package com.example.citucares.features.auth

import com.example.citucares.core.network.RetrofitClient
import com.example.citucares.core.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthPresenter(private val view: LoginActivity) {

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Please fill all fields")
            return
        }

        view.showLoading(true)

        val request = LoginRequest(email, password)

        RetrofitClient.instance.loginUser(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    view.showLoading(false)

                    if (response.isSuccessful && response.body() != null) {

                        val user = response.body()!!

                        // ✅ FIX: use email instead of token
                        TokenManager.save(view, user.email)

                        view.onLoginSuccess()

                    } else {
                        view.showError("Invalid credentials ❌")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    view.showLoading(false)
                    view.showError("Server error ❌")
                }
            })
    }
}