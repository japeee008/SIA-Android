package com.example.citucares.features.auth

import android.util.Log
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

                        Log.d(
                            "LOGIN_SUCCESS",
                            "User ID: ${user.userId}, Email: ${user.email}, Name: ${user.fname} ${user.lname}, Role: ${user.role}"
                        )

                        TokenManager.save(view, user.email)

                        view.onLoginSuccess(user)
                    } else {
                        Log.e("LOGIN_ERROR", "Code: ${response.code()}")
                        Log.e("LOGIN_ERROR", "Error body: ${response.errorBody()?.string()}")

                        view.showError("Invalid credentials ❌")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    view.showLoading(false)

                    Log.e("LOGIN_ERROR", "Failure: ${t.message}", t)

                    view.showError("Server error ❌")
                }
            })
    }
}