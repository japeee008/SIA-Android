package com.example.citucares.features.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.citucares.R
import com.example.citucares.features.chat.ChatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var loading: ProgressBar

    lateinit var presenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginBtn = findViewById(R.id.loginBtn)
        loading = findViewById(R.id.loading)

        presenter = AuthPresenter(this)

        loginBtn.setOnClickListener {
            presenter.login(
                emailInput.text.toString(),
                passwordInput.text.toString()
            )
        }

        findViewById<TextView>(R.id.goRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    fun showLoading(isLoading: Boolean) {
        loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginBtn.isEnabled = !isLoading
    }

    fun onLoginSuccess() {
        Toast.makeText(this, "Login Success ✅", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}