package com.example.citucares.features.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.citucares.R
import com.example.citucares.features.auth.LoginActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var backBtn: TextView
    private lateinit var profileImage: ImageView
    private lateinit var userNameText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var institutionalIdText: TextView
    private lateinit var emailText: TextView
    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var logoutBtn: Button

    private var userId: Long = -1L
    private var email: String = ""
    private var fname: String = ""
    private var lname: String = ""
    private var role: String = ""
    private var institutionalId: String = ""

    private val supabaseBaseProfileUrl =
        "https://bnygvxesmbiumvwrjjmy.supabase.co/storage/v1/object/public/profile-photos/avatars"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userId = intent.getLongExtra("USER_ID", -1L)
        email = intent.getStringExtra("EMAIL") ?: ""
        fname = intent.getStringExtra("FNAME") ?: ""
        lname = intent.getStringExtra("LNAME") ?: ""
        role = intent.getStringExtra("ROLE") ?: ""
        institutionalId = intent.getStringExtra("INSTITUTIONAL_ID") ?: ""

        backBtn = findViewById(R.id.backBtn)
        profileImage = findViewById(R.id.profileImage)
        userNameText = findViewById(R.id.userNameText)
        userRoleText = findViewById(R.id.userRoleText)
        institutionalIdText = findViewById(R.id.institutionalIdText)
        emailText = findViewById(R.id.emailText)
        firstNameText = findViewById(R.id.firstNameText)
        lastNameText = findViewById(R.id.lastNameText)
        logoutBtn = findViewById(R.id.logoutBtn)

        setupUserInfo()
        setupActions()
    }

    private fun setupUserInfo() {
        val fullName = "$fname $lname".trim()

        val displayName = when {
            fullName.isNotBlank() -> fullName
            email.isNotBlank() -> email
            else -> "User"
        }

        userNameText.text = displayName
        userRoleText.text = formatRole(role)

        institutionalIdText.text = if (institutionalId.isNotBlank()) institutionalId else "N/A"
        emailText.text = if (email.isNotBlank()) email else "Not available"
        firstNameText.text = if (fname.isNotBlank()) fname else "Not available"
        lastNameText.text = if (lname.isNotBlank()) lname else "Not available"

        loadProfilePhoto()
    }

    private fun loadProfilePhoto() {
        if (userId == -1L) {
            profileImage.setImageResource(R.drawable.citu_logo)
            return
        }

        val profilePhotoUrl = "$supabaseBaseProfileUrl/$userId.jpg?t=${System.currentTimeMillis()}"

        Glide.with(this)
            .load(profilePhotoUrl)
            .placeholder(R.drawable.citu_logo)
            .error(R.drawable.citu_logo)
            .circleCrop()
            .into(profileImage)
    }

    private fun formatRole(value: String): String {
        return when (value.lowercase()) {
            "user" -> "Student"
            "student" -> "Student"
            "admin" -> "Admin"
            "super_admin" -> "Superadmin"
            "superadmin" -> "Superadmin"
            else -> if (value.isNotBlank()) value.replaceFirstChar { it.uppercase() } else "Student"
        }
    }

    private fun setupActions() {
        backBtn.setOnClickListener {
            finish()
        }

        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}