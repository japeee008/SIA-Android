package com.example.citucares

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var loading: ProgressBar

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

        RetrofitClient.instance.loginUser(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    loading.visibility = View.GONE
                    loginBtn.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {

                        val user = response.body()!!

// Save session (you already have this)
                        TokenManager.save(this@MainActivity, user.email)

                        toast("Login Success ✅")

// 🚀 NAVIGATE TO CHAT
                        val intent = Intent(this@MainActivity, ChatActivity::class.java)
                        startActivity(intent)

// Optional: close login screen so user can't go back
                        finish()

                    } else {
                        toast("Invalid credentials ❌")
                        println(response.errorBody()?.string())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loading.visibility = View.GONE
                    loginBtn.isEnabled = true
                    toast("Server error ❌")
                }
            })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}