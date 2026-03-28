package com.example.citucares

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.citucares.api.RetrofitClient
import com.example.citucares.model.LoginRequest
import com.example.citucares.model.LoginResponse
import com.example.citucares.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent

class MainActivity : AppCompatActivity() {

    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var loading: ProgressBar

    private val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJueWd2eGVzbWJpdW12d3Jqam15Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA5ODg1MjIsImV4cCI6MjA4NjU2NDUyMn0.D4JN12XSWvXkUriWeYB5mCXkqxY8tB_pAX4bcWr3NNE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginBtn = findViewById(R.id.loginBtn)
        loading = findViewById(R.id.loading)

        loginBtn.setOnClickListener {
            loginUser()
        }

        val goRegister = findViewById<TextView>(R.id.goRegister)

        goRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Please fill all fields")
            return
        }

        loading.visibility = View.VISIBLE
        loginBtn.isEnabled = false

        val request = LoginRequest(email, password)

        RetrofitClient.instance.login(
            API_KEY,
            "application/json",
            "application/json",
            request
        )
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    loading.visibility = View.GONE
                    loginBtn.isEnabled = true

                    if (response.isSuccessful) {
                        val token = response.body()?.access_token

                        if (token != null) {
                            TokenManager.save(this@MainActivity, token)
                            toast("Login Success ✅")
                        } else {
                            toast("Login failed ❌ (no token)")
                        }

                    } else {
                        toast("Invalid credentials ❌")
                        println("CODE: ${response.code()}")
                        println("ERROR: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loading.visibility = View.GONE
                    loginBtn.isEnabled = true
                    toast("No internet ❌")
                }
            })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}