package com.example.citucares.features.chat

import android.util.Log
import com.example.citucares.core.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatPresenter(private val view: ChatContract.View) : ChatContract.Presenter {

    private var sessionId: Long? = null

    override fun sendMessage(message: String) {
        if (message.isBlank()) {
            view.showError("Please enter a message")
            return
        }

        view.addUserMessage(message)
        view.clearInput()
        view.showLoading(true)

        val request = ChatRequest(
            message = message,
            sessionId = sessionId
        )

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
                    } else {
                        Log.e("CHAT_ERROR", "Code: ${response.code()}")
                        Log.e("CHAT_ERROR", "Error body: ${response.errorBody()?.string()}")

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
}