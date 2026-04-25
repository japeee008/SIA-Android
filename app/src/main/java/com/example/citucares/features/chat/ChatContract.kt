package com.example.citucares.features.chat

interface ChatContract {

    interface View {
        fun addUserMessage(message: String)
        fun addBotMessage(message: String)
        fun clearInput()
        fun showLoading(isLoading: Boolean)
        fun showError(msg: String)
    }

    interface Presenter {
        fun sendMessage(message: String)
    }
}