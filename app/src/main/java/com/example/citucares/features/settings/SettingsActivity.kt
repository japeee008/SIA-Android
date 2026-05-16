package com.example.citucares.features.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.citucares.R
import com.example.citucares.core.network.RetrofitClient
import com.example.citucares.features.auth.LoginActivity
import okhttp3.Call as OkHttpCall
import okhttp3.Callback as OkHttpCallback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response as OkHttpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private lateinit var backBtn: TextView
    private lateinit var profileImage: ImageView
    private lateinit var changePhotoBtn: TextView
    private lateinit var userNameText: TextView
    private lateinit var userRoleText: TextView
    private lateinit var institutionalIdText: TextView
    private lateinit var emailText: TextView

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var editProfileBtn: Button
    private lateinit var cancelEditBtn: Button
    private lateinit var saveProfileBtn: Button
    private lateinit var editActionContainer: LinearLayout

    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var updatePasswordBtn: Button

    private lateinit var logoutBtn: Button

    private var userId: Long = -1L
    private var email: String = ""
    private var fname: String = ""
    private var lname: String = ""
    private var role: String = ""
    private var institutionalId: String = ""

    private var isEditingProfile = false

    private val supabaseUrl = "https://bnygvxesmbiumvwrjjmy.supabase.co"
    private val supabaseAnonKey = "sb_publishable_6Yzn30ASGK1HVhmAjzw0sw_kjXljVDS"
    private val supabaseBucket = "profile-photos"

    private val supabaseBaseProfileUrl =
        "$supabaseUrl/storage/v1/object/public/$supabaseBucket/avatars"

    private val httpClient = OkHttpClient()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uploadProfilePhoto(uri)
            }
        }

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
        changePhotoBtn = findViewById(R.id.changePhotoBtn)
        userNameText = findViewById(R.id.userNameText)
        userRoleText = findViewById(R.id.userRoleText)
        institutionalIdText = findViewById(R.id.institutionalIdText)
        emailText = findViewById(R.id.emailText)

        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        editProfileBtn = findViewById(R.id.editProfileBtn)
        cancelEditBtn = findViewById(R.id.cancelEditBtn)
        saveProfileBtn = findViewById(R.id.saveProfileBtn)
        editActionContainer = findViewById(R.id.editActionContainer)

        currentPasswordInput = findViewById(R.id.currentPasswordInput)
        newPasswordInput = findViewById(R.id.newPasswordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn)

        logoutBtn = findViewById(R.id.logoutBtn)

        setupUserInfo()
        setupActions()
        setProfileEditMode(false)
    }

    private fun setupUserInfo() {
        updateDisplayedName()

        userRoleText.text = formatRole(role)
        institutionalIdText.text = if (institutionalId.isNotBlank()) institutionalId else "N/A"
        emailText.text = if (email.isNotBlank()) email else "Not available"

        firstNameInput.setText(fname)
        lastNameInput.setText(lname)

        loadProfilePhoto()
    }

    private fun updateDisplayedName() {
        val fullName = "$fname $lname".trim()

        val displayName = when {
            fullName.isNotBlank() -> fullName
            email.isNotBlank() -> email
            else -> "User"
        }

        userNameText.text = displayName
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

    private fun setupActions() {
        backBtn.setOnClickListener {
            finish()
        }

        changePhotoBtn.setOnClickListener {
            if (userId == -1L) {
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pickImageLauncher.launch("image/*")
        }

        profileImage.setOnClickListener {
            if (userId == -1L) {
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pickImageLauncher.launch("image/*")
        }

        editProfileBtn.setOnClickListener {
            setProfileEditMode(true)
        }

        cancelEditBtn.setOnClickListener {
            firstNameInput.setText(fname)
            lastNameInput.setText(lname)
            setProfileEditMode(false)
        }

        saveProfileBtn.setOnClickListener {
            saveProfileChanges()
        }

        updatePasswordBtn.setOnClickListener {
            changePassword()
        }

        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun uploadProfilePhoto(uri: Uri) {
        if (userId == -1L) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromUri(uri) ?: "image/jpeg"

        if (!mimeType.startsWith("image/")) {
            Toast.makeText(this, "Only image files are allowed.", Toast.LENGTH_SHORT).show()
            return
        }

        val imageBytes = try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            null
        }

        if (imageBytes == null) {
            Toast.makeText(this, "Failed to read selected image.", Toast.LENGTH_SHORT).show()
            return
        }

        changePhotoBtn.isEnabled = false
        profileImage.isEnabled = false
        Toast.makeText(this, "Uploading profile photo...", Toast.LENGTH_SHORT).show()

        val filePath = "avatars/$userId.jpg"
        val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$supabaseUrl/storage/v1/object/$supabaseBucket/$filePath")
            .put(requestBody)
            .addHeader("apikey", supabaseAnonKey)
            .addHeader("Authorization", "Bearer $supabaseAnonKey")
            .addHeader("Content-Type", mimeType)
            .addHeader("x-upsert", "true")
            .build()

        httpClient.newCall(request).enqueue(object : OkHttpCallback {
            override fun onFailure(call: OkHttpCall, e: IOException) {
                runOnUiThread {
                    changePhotoBtn.isEnabled = true
                    profileImage.isEnabled = true

                    Toast.makeText(
                        this@SettingsActivity,
                        "Upload failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: OkHttpCall, response: OkHttpResponse) {
                response.use {
                    runOnUiThread {
                        changePhotoBtn.isEnabled = true
                        profileImage.isEnabled = true

                        if (response.isSuccessful) {
                            Glide.get(this@SettingsActivity).clearMemory()
                            loadProfilePhoto()

                            Toast.makeText(
                                this@SettingsActivity,
                                "Profile photo updated.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SettingsActivity,
                                "Upload failed: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri))

        return if (extension != null) "image/$extension" else null
    }

    private fun setProfileEditMode(enabled: Boolean) {
        isEditingProfile = enabled

        firstNameInput.isEnabled = enabled
        lastNameInput.isEnabled = enabled

        editProfileBtn.visibility = if (enabled) View.GONE else View.VISIBLE
        editActionContainer.visibility = if (enabled) View.VISIBLE else View.GONE

        if (enabled) {
            firstNameInput.requestFocus()
        }
    }

    private fun saveProfileChanges() {
        val newFname = firstNameInput.text.toString().trim()
        val newLname = lastNameInput.text.toString().trim()

        if (newFname.isEmpty() || newLname.isEmpty()) {
            Toast.makeText(this, "First name and last name are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == -1L) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
            return
        }

        saveProfileBtn.isEnabled = false
        cancelEditBtn.isEnabled = false
        saveProfileBtn.text = "Saving..."

        val request = UpdateProfileRequest(
            fname = newFname,
            lname = newLname,
            email = email,
            role = role
        )

        RetrofitClient.instance.updateUserProfile(userId, request)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    saveProfileBtn.isEnabled = true
                    cancelEditBtn.isEnabled = true
                    saveProfileBtn.text = "Save"

                    if (response.isSuccessful && response.body() != null) {
                        val updatedUser = response.body()!!

                        fname = updatedUser.fname
                        lname = updatedUser.lname
                        email = updatedUser.email
                        role = updatedUser.role
                        institutionalId = updatedUser.institutionalId ?: institutionalId

                        updateDisplayedName()

                        firstNameInput.setText(fname)
                        lastNameInput.setText(lname)
                        emailText.text = if (email.isNotBlank()) email else "Not available"
                        institutionalIdText.text =
                            if (institutionalId.isNotBlank()) institutionalId else "N/A"
                        userRoleText.text = formatRole(role)

                        setProfileEditMode(false)

                        Toast.makeText(
                            this@SettingsActivity,
                            "Profile updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Failed to update profile.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    saveProfileBtn.isEnabled = true
                    cancelEditBtn.isEnabled = true
                    saveProfileBtn.text = "Save"

                    Toast.makeText(
                        this@SettingsActivity,
                        "Server error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun changePassword() {
        val currentPassword = currentPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Please fill all password fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == -1L) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
            return
        }

        updatePasswordBtn.isEnabled = false
        updatePasswordBtn.text = "Updating..."

        val request = ChangePasswordRequest(
            userId = userId,
            currentPassword = currentPassword,
            newPassword = newPassword
        )

        RetrofitClient.instance.changePassword(request)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    updatePasswordBtn.isEnabled = true
                    updatePasswordBtn.text = "Update Password"

                    if (response.isSuccessful) {
                        currentPasswordInput.setText("")
                        newPasswordInput.setText("")
                        confirmPasswordInput.setText("")

                        Toast.makeText(
                            this@SettingsActivity,
                            "Password updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorMessage = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            null
                        }

                        Toast.makeText(
                            this@SettingsActivity,
                            errorMessage ?: "Failed to update password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    updatePasswordBtn.isEnabled = true
                    updatePasswordBtn.text = "Update Password"

                    Toast.makeText(
                        this@SettingsActivity,
                        "Server error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
}