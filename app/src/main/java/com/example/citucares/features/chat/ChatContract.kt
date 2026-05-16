package com.example.citucares.features.chat

interface ChatContract {

    interface View {
        fun addUserMessage(message: String)
        fun addBotMessage(message: String)
        fun clearInput()
        fun clearMessages()
        fun showGreeting()
        fun showLoading(isLoading: Boolean)
        fun showError(msg: String)
        fun showSessions(sessions: List<ChatSession>)
        fun closeSidebar()
    }

    interface Presenter {
        fun sendMessage(message: String)
        fun loadSessions()
        fun loadSessionMessages(sessionId: Long)
        fun startNewChat()
    }
}