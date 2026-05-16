package com.example.citucares.features.auth

import android.content.Intent
import android.graphics.Color
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

        window.statusBarColor = Color.parseColor("#8B0000")
        window.navigationBarColor = Color.BLACK

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginBtn = findViewById(R.id.loginBtn)
        loading = findViewById(R.id.loading)

        emailInput.setTextColor(Color.parseColor("#111111"))
        emailInput.setHintTextColor(Color.parseColor("#777777"))
        emailInput.alpha = 1f

        passwordInput.setTextColor(Color.parseColor("#111111"))
        passwordInput.setHintTextColor(Color.parseColor("#777777"))
        passwordInput.alpha = 1f

        presenter = AuthPresenter(this)

        loginBtn.setOnClickListener {
            presenter.login(
                emailInput.text.toString().trim(),
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
        loginBtn.alpha = if (isLoading) 0.85f else 1f

        emailInput.isEnabled = true
        passwordInput.isEnabled = true

        emailInput.alpha = 1f
        passwordInput.alpha = 1f

        emailInput.setTextColor(Color.parseColor("#111111"))
        emailInput.setHintTextColor(Color.parseColor("#777777"))

        passwordInput.setTextColor(Color.parseColor("#111111"))
        passwordInput.setHintTextColor(Color.parseColor("#777777"))
    }

    fun onLoginSuccess(user: LoginResponse) {
        Toast.makeText(this, "Login Success ✅", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("USER_ID", user.userId)
        intent.putExtra("EMAIL", user.email)
        intent.putExtra("FNAME", user.fname)
        intent.putExtra("LNAME", user.lname)
        intent.putExtra("ROLE", user.role)
        intent.putExtra("INSTITUTIONAL_ID", user.institutionalId ?: "")

        startActivity(intent)
        finish()
    }

    fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}