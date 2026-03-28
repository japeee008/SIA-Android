package com.example.citucares

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.citucares.api.RetrofitClient
import com.example.citucares.model.LoginRequest
import com.example.citucares.model.LoginResponse
import retrofit2.*

class RegisterActivity : AppCompatActivity() {

    lateinit var studentIdInput: EditText
    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var fnameInput: EditText
    lateinit var lnameInput: EditText   // ✅ added
    lateinit var miInput: EditText      // ✅ added
    lateinit var registerBtn: Button
    lateinit var cancelBtn: Button

    private val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJueWd2eGVzbWJpdW12d3Jqam15Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA5ODg1MjIsImV4cCI6MjA4NjU2NDUyMn0.D4JN12XSWvXkUriWeYB5mCXkqxY8tB_pAX4bcWr3NNE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        studentIdInput = findViewById(R.id.studentIdInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        fnameInput = findViewById(R.id.fnameInput)
        lnameInput = findViewById(R.id.lnameInput)   // ✅ added
        miInput = findViewById(R.id.miInput)         // ✅ added
        registerBtn = findViewById(R.id.registerBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener { finish() }

        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Fill all fields")
            return
        }

        val request = LoginRequest(email, password)

        RetrofitClient.instance.register(
            API_KEY,
            "application/json",
            "application/json",
            request
        ).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                println("REGISTER CODE: ${response.code()}")
                println("REGISTER BODY: ${response.body()}")
                println("REGISTER ERROR: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {

                    val userId = response.body()!!.user!!.id
                    val userEmail = response.body()!!.user!!.email
                    val accessToken = response.body()!!.access_token

                    if (accessToken != null) {
                        insertUserToTable(userId, userEmail, accessToken)
                        toast("Registered ✅")
                        finish()
                    } else {
                        toast("No access token ❌")
                    }

                } else {
                    toast("Register failed ❌")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                println("REGISTER FAILURE: ${t.message}")
                toast("No internet ❌")
            }
        })
    }

    private fun insertUserToTable(userId: String, email: String, accessToken: String) {

        val data = mapOf(
            "user_id" to userId,
            "email" to email,
            "fname" to fnameInput.text.toString(),
            "lname" to lnameInput.text.toString(),              // ✅ added
            "middle_initial" to miInput.text.toString(),        // ✅ added
            "institutional_id" to studentIdInput.text.toString()
        )

        RetrofitClient.instance.insertUser(
            API_KEY,
            "Bearer $accessToken",
            "application/json",
            "return=representation",
            data
        ).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                println("INSERT CODE: ${response.code()}")
                println("INSERT ERROR: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    println("Inserted to users table ✅")
                } else {
                    println("Insert failed ❌")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("INSERT FAILURE: ${t.message}")
            }
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}