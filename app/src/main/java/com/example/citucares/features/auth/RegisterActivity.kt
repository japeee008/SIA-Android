package com.example.citucares.features.auth

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.citucares.R
import com.example.citucares.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var studentIdInput: EditText
    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var confirmPasswordInput: EditText
    lateinit var fnameInput: EditText
    lateinit var lnameInput: EditText
    lateinit var miInput: EditText
    lateinit var registerBtn: Button
    lateinit var cancelBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Keeps the top system status bar consistent with CITU-CARE branding.
        window.statusBarColor = Color.parseColor("#8B0000")

        studentIdInput = findViewById(R.id.studentIdInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        fnameInput = findViewById(R.id.fnameInput)
        lnameInput = findViewById(R.id.lnameInput)
        miInput = findViewById(R.id.miInput)
        registerBtn = findViewById(R.id.registerBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        cancelBtn.setOnClickListener {
            finish()
        }

        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val studentId = studentIdInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        val fname = fnameInput.text.toString().trim()
        val lname = lnameInput.text.toString().trim()
        val mi = miInput.text.toString().trim()

        if (studentId.isEmpty() || email.isEmpty() || password.isEmpty()
            || confirmPassword.isEmpty() || fname.isEmpty() || lname.isEmpty()
        ) {
            toast("Please fill all required fields ❌")
            return
        }

        if (password != confirmPassword) {
            toast("Passwords do not match ❌")
            return
        }

        val request = RegisterRequest(
            studentId = studentId,
            fname = fname,
            lname = lname,
            middleInitial = mi,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )

        println("REQUEST DEBUG: $request")

        RetrofitClient.instance.registerUser(request)
            .enqueue(object : Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    if (response.isSuccessful) {
                        toast("Registered Successfully ✅")
                        finish()
                    } else {
                        toast("Registration Failed ❌")
                        println("ERROR: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    toast("Server Error ❌")
                    println("FAIL: ${t.message}")
                }
            })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}