package com.example.citucares.features.chat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.citucares.R
import com.example.citucares.features.auth.LoginActivity
import com.example.citucares.features.settings.SettingsActivity

class ChatActivity : AppCompatActivity(), ChatContract.View {

    private lateinit var presenter: ChatContract.Presenter

    private lateinit var messageInput: EditText
    private lateinit var sendBtn: ImageButton
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScroll: ScrollView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuBtn: ImageView
    private lateinit var profileBtn: ImageView

    private lateinit var backBtn: TextView
    private lateinit var logoutBtn: TextView
    private lateinit var newChatBtn: Button
    private lateinit var settingsBtn: Button
    private lateinit var sessionListContainer: LinearLayout

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
        setContentView(R.layout.activity_chat)

        userId = intent.getLongExtra("USER_ID", -1L)
        email = intent.getStringExtra("EMAIL") ?: ""
        fname = intent.getStringExtra("FNAME") ?: ""
        lname = intent.getStringExtra("LNAME") ?: ""
        role = intent.getStringExtra("ROLE") ?: ""
        institutionalId = intent.getStringExtra("INSTITUTIONAL_ID") ?: ""

        Log.d("CHAT_USER_ID", "Received userId: $userId")
        Log.d("CHAT_INSTITUTIONAL_ID", "Received institutionalId: $institutionalId")

        if (userId == -1L) {
            Toast.makeText(this, "User session error. Please login again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        presenter = ChatPresenter(this, userId)

        messageInput = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        chatContainer = findViewById(R.id.chatContainer)
        chatScroll = findViewById(R.id.chatScroll)

        drawerLayout = findViewById(R.id.drawerLayout)
        menuBtn = findViewById(R.id.menuBtn)
        profileBtn = findViewById(R.id.profileBtn)

        backBtn = findViewById(R.id.backBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        newChatBtn = findViewById(R.id.newChatBtn)
        settingsBtn = findViewById(R.id.settingsBtn)
        sessionListContainer = findViewById(R.id.sessionListContainer)

        setupProfileButton()
        showGreeting()

        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            presenter.loadSessions()
        }

        backBtn.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        newChatBtn.setOnClickListener {
            presenter.startNewChat()
        }

        settingsBtn.setOnClickListener {
            openSettings()
        }

        profileBtn.setOnClickListener {
            openSettings()
        }

        logoutBtn.setOnClickListener {
            logout()
        }

        sendBtn.setOnClickListener {
            presenter.sendMessage(messageInput.text.toString())
        }

        presenter.loadSessions()
    }

    private fun setupProfileButton() {
        if (userId == -1L) {
            profileBtn.setImageResource(R.drawable.citu_logo)
            return
        }

        val profilePhotoUrl = "$supabaseBaseProfileUrl/$userId.jpg?t=${System.currentTimeMillis()}"

        Glide.with(this)
            .load(profilePhotoUrl)
            .placeholder(R.drawable.citu_logo)
            .error(R.drawable.citu_logo)
            .circleCrop()
            .into(profileBtn)
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("EMAIL", email)
        intent.putExtra("FNAME", fname)
        intent.putExtra("LNAME", lname)
        intent.putExtra("ROLE", role)
        intent.putExtra("INSTITUTIONAL_ID", institutionalId)
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun addUserMessage(message: String) {
        val wrapper = LinearLayout(this)
        wrapper.orientation = LinearLayout.HORIZONTAL
        wrapper.gravity = Gravity.END
        wrapper.setPadding(8, 8, 8, 8)

        val wrapperParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        wrapperParams.setMargins(0, 10, 0, 10)
        wrapper.layoutParams = wrapperParams

        val bubble = TextView(this)
        bubble.text = message
        bubble.setTextColor(Color.BLACK)
        bubble.textSize = 16f
        bubble.setPadding(24, 18, 24, 18)
        bubble.setBackgroundResource(R.drawable.bg_user_bubble)

        val params = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.75).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        bubble.layoutParams = params

        wrapper.addView(bubble)
        chatContainer.addView(wrapper)
        scrollToBottom()
    }

    override fun addBotMessage(message: String) {
        val wrapper = LinearLayout(this)
        wrapper.orientation = LinearLayout.HORIZONTAL
        wrapper.gravity = Gravity.START
        wrapper.setPadding(8, 8, 8, 8)

        val wrapperParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        wrapperParams.setMargins(0, 10, 0, 10)
        wrapper.layoutParams = wrapperParams

        val logo = ImageView(this)
        logo.setImageResource(R.drawable.citu_logo)

        val logoParams = LinearLayout.LayoutParams(50, 50)
        logoParams.setMargins(0, 10, 10, 0)
        logo.layoutParams = logoParams

        val bubble = TextView(this)
        bubble.text = message
        bubble.setTextColor(Color.BLACK)
        bubble.textSize = 16f
        bubble.setPadding(24, 18, 24, 18)
        bubble.setBackgroundResource(R.drawable.bg_bot_bubble)

        val bubbleParams = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.68).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        bubble.layoutParams = bubbleParams

        wrapper.addView(logo)
        wrapper.addView(bubble)
        chatContainer.addView(wrapper)
        scrollToBottom()
    }

    override fun clearInput() {
        messageInput.setText("")
    }

    override fun clearMessages() {
        chatContainer.removeAllViews()
    }

    override fun showGreeting() {
        val greeting = TextView(this)
        greeting.text = "Hello! 👋 I'm your chatbot assistant.\nHow can I help you today?"
        greeting.textSize = 15f
        greeting.setTextColor(Color.rgb(68, 68, 68))
        greeting.gravity = Gravity.CENTER
        greeting.setPadding(12, 12, 12, 28)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 10, 0, 20)
        greeting.layoutParams = params

        chatContainer.addView(greeting)
    }

    override fun showLoading(isLoading: Boolean) {
        sendBtn.isEnabled = !isLoading
        messageInput.isEnabled = !isLoading
    }

    override fun showError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showSessions(sessions: List<ChatSession>) {
        sessionListContainer.removeAllViews()

        if (sessions.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No recent chats yet."
            emptyText.setTextColor(Color.WHITE)
            emptyText.textSize = 13f
            emptyText.setPadding(8, 12, 8, 12)
            sessionListContainer.addView(emptyText)
            return
        }

        sessions.forEach { session ->
            val sessionBtn = TextView(this)

            sessionBtn.text = "▢  ${session.title}"
            sessionBtn.setTextColor(Color.WHITE)
            sessionBtn.textSize = 14f
            sessionBtn.maxLines = 1
            sessionBtn.setPadding(8, 14, 8, 14)
            sessionBtn.setBackgroundColor(Color.TRANSPARENT)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 2, 0, 2)
            sessionBtn.layoutParams = params

            sessionBtn.setOnClickListener {
                presenter.loadSessionMessages(session.sessionId)
            }

            sessionListContainer.addView(sessionBtn)
        }
    }

    override fun closeSidebar() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun scrollToBottom() {
        chatScroll.post {
            chatScroll.fullScroll(View.FOCUS_DOWN)
        }
    }
}