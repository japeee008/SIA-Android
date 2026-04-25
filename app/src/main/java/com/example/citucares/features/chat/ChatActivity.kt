package com.example.citucares.features.chat

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.citucares.R

class ChatActivity : AppCompatActivity(), ChatContract.View {

    private lateinit var presenter: ChatContract.Presenter

    private lateinit var messageInput: EditText
    private lateinit var sendBtn: ImageButton
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScroll: ScrollView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuBtn: ImageView

    private lateinit var sidebarTitle: TextView
    private lateinit var backBtn: TextView
    private lateinit var mainSettingsMenu: LinearLayout
    private lateinit var accountBtn: TextView
    private lateinit var logoutBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        presenter = ChatPresenter(this)

        messageInput = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        chatContainer = findViewById(R.id.chatContainer)
        chatScroll = findViewById(R.id.chatScroll)

        drawerLayout = findViewById(R.id.drawerLayout)
        menuBtn = findViewById(R.id.menuBtn)

        sidebarTitle = findViewById(R.id.sidebarTitle)
        backBtn = findViewById(R.id.backBtn)
        mainSettingsMenu = findViewById(R.id.mainSettingsMenu)
        accountBtn = findViewById(R.id.accountBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        backBtn.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        accountBtn.setOnClickListener {
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            finish()
        }

        sendBtn.setOnClickListener {
            presenter.sendMessage(messageInput.text.toString())
        }
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
        bubble.setTextSize(16f)
        bubble.setPadding(24, 18, 24, 18)
        bubble.setBackgroundResource(R.drawable.bg_user_bubble)

        val params = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
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
        bubble.setTextSize(16f)
        bubble.setPadding(24, 18, 24, 18)
        bubble.setBackgroundResource(R.drawable.bg_bot_bubble)

        val bubbleParams = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.78).toInt(),
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

    override fun showLoading(isLoading: Boolean) {
        // optional
    }

    override fun showError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun scrollToBottom() {
        chatScroll.post {
            chatScroll.fullScroll(View.FOCUS_DOWN)
        }
    }
}