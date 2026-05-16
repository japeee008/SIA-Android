package com.example.citucares.features.chat

import android.util.Log
import com.example.citucares.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatPresenter(
    private val view: ChatContract.View,
    private val userId: Long
) : ChatContract.Presenter {

    private var sessionId: Long? = null

    override fun sendMessage(message: String) {
        if (message.isBlank()) {
            view.showError("Please enter a message")
            return
        }

        if (userId == -1L) {
            view.showError("User session error. Please login again.")
            return
        }

        view.addUserMessage(message)
        view.clearInput()
        view.showLoading(true)

        val request = ChatRequest(
            message = message,
            userId = userId,
            sessionId = sessionId
        )

        Log.d("CHAT_REQUEST", "Request: $request")

        RetrofitClient.instance.sendMessage(request)
            .enqueue(object : Callback<ChatResponse> {

                override fun onResponse(
                    call: Call<ChatResponse>,
                    response: Response<ChatResponse>
                ) {
                    view.showLoading(false)

                    if (response.isSuccessful && response.body() != null) {
                        val chatResponse = response.body()!!

                        sessionId = chatResponse.sessionId
                        view.addBotMessage(chatResponse.reply)

                        loadSessions()
                    } else {
                        val errorBody = response.errorBody()?.string()

                        Log.e("CHAT_ERROR", "Code: ${response.code()}")
                        Log.e("CHAT_ERROR", "Error body: $errorBody")

                        view.addBotMessage("Sorry, I couldn't process your message.")
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    view.showLoading(false)

                    Log.e("CHAT_ERROR", "Failure: ${t.message}", t)

                    view.addBotMessage("Server error. Please make sure the backend is running.")
                }
            })
    }

    override fun loadSessions() {
        if (userId == -1L) {
            view.showError("User session error. Please login again.")
            return
        }

        RetrofitClient.instance.getChatSessions(userId)
            .enqueue(object : Callback<List<ChatSession>> {

                override fun onResponse(
                    call: Call<List<ChatSession>>,
                    response: Response<List<ChatSession>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        view.showSessions(response.body()!!)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SESSION_ERROR", "Code: ${response.code()}")
                        Log.e("SESSION_ERROR", "Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<ChatSession>>, t: Throwable) {
                    Log.e("SESSION_ERROR", "Failure: ${t.message}", t)
                    view.showError("Unable to load chat sessions.")
                }
            })
    }

    override fun loadSessionMessages(sessionId: Long) {
        this.sessionId = sessionId

        view.clearMessages()
        view.showLoading(true)

        RetrofitClient.instance.getChatHistory(userId, sessionId)
            .enqueue(object : Callback<List<ChatHistoryMessage>> {

                override fun onResponse(
                    call: Call<List<ChatHistoryMessage>>,
                    response: Response<List<ChatHistoryMessage>>
                ) {
                    view.showLoading(false)

                    if (response.isSuccessful && response.body() != null) {
                        val messages = response.body()!!

                        if (messages.isEmpty()) {
                            view.showGreeting()
                        } else {
                            messages.forEach { msg ->
                                if (!msg.messageText.isNullOrBlank()) {
                                    view.addUserMessage(msg.messageText)
                                }

                                if (!msg.botReply.isNullOrBlank()) {
                                    view.addBotMessage(msg.botReply)
                                }
                            }
                        }

                        view.closeSidebar()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("HISTORY_ERROR", "Code: ${response.code()}")
                        Log.e("HISTORY_ERROR", "Error body: $errorBody")
                        view.showError("Unable to load this conversation.")
                    }
                }

                override fun onFailure(call: Call<List<ChatHistoryMessage>>, t: Throwable) {
                    view.showLoading(false)
                    Log.e("HISTORY_ERROR", "Failure: ${t.message}", t)
                    view.showError("Unable to load this conversation.")
                }
            })
    }

    override fun startNewChat() {
        sessionId = null
        view.clearMessages()
        view.showGreeting()
        view.closeSidebar()
    }
}