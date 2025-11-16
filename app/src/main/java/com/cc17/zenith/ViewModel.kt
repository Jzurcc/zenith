package com.cc17.zenith

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    // No API key needed, all in Firebase
    private val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-2.5-flash"
    )
    private val _messageList = MutableLiveData<List<ChatMessage>>()
    val messageList: LiveData<List<ChatMessage>> get() = _messageList

    private val currentMessages = mutableListOf<ChatMessage>()

    fun sendMessage(message: String) {
        Log.d("ChatViewModel", "sendMessage called with: $message")
        currentMessages.add(ChatMessage(message, true))
        _messageList.value = currentMessages.toList()
        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Calling Gemini API...")
                val response = generativeModel.generateContent(message)

                response.text?.let { botResponse ->
                    currentMessages.add(ChatMessage(botResponse, false))
                    _messageList.value = currentMessages.toList()
                    Log.d("ChatViewModel", "Gemini response: $botResponse")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "API Error: ${e.message}", e)
                currentMessages.add(ChatMessage("Error: ${e.message}", false))
                _messageList.value = currentMessages.toList()
            }
        }
    }
}