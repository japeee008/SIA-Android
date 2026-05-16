package com.example.citucares.features.auth

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.graphics.Typeface
import android.text.InputType
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
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
        setContentView(R.layout.activity_login)

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

        findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
            showForgotPasswordDialog()
        }

        findViewById<TextView>(R.id.goRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showForgotPasswordDialog() {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(48, 32, 48, 16)
        container.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "Forgot Password"
        title.textSize = 20f
        title.setTextColor(Color.parseColor("#111111"))
        title.setTypeface(null, Typeface.BOLD)

        val message = TextView(this)
        message.text = "Enter your CIT email. You will be redirected to the reset password page."
        message.textSize = 14f
        message.setTextColor(Color.parseColor("#555555"))
        message.setPadding(0, 12, 0, 20)

        val input = EditText(this)
        input.hint = "Enter your CIT email"
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.setSingleLine(true)
        input.setTextColor(Color.parseColor("#111111"))
        input.setHintTextColor(Color.parseColor("#888888"))
        input.setBackgroundResource(R.drawable.bg_input)
        input.setPadding(24, 0, 24, 0)

        val inputParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            52
        )
        input.layoutParams = inputParams

        container.addView(title)
        container.addView(message)
        container.addView(input)

        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Continue", null)
            .create()

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.white)

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#8B0000"))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#8B0000"))

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                val email = input.text.toString().trim()

                if (email.isEmpty()) {
                    input.error = "Email is required"
                    return@setOnClickListener
                }

                dialog.dismiss()
                presenter.forgotPassword(email)
            }
        }

        dialog.show()
    }

    fun openResetPasswordPage(token: String) {
        Toast.makeText(this, "Opening reset password page", Toast.LENGTH_SHORT).show()

        val resetUrl = "https://citucare-frontend.vercel.app/reset-password?token=$token"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resetUrl))

        startActivity(intent)
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