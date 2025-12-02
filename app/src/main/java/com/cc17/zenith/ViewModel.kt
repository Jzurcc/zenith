package com.cc17.zenith

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-2.5-flash"
    )

    // START CHAT: This enables multi-turn history (Memory)
    private val chat = generativeModel.startChat()

    private val _messageList = MutableLiveData<List<ChatMessage>>()
    val messageList: LiveData<List<ChatMessage>> get() = _messageList

    private val currentMessages = mutableListOf<ChatMessage>()

    fun sendMessage(message: String) {
        // 1. Add User Message
        currentMessages.add(ChatMessage("You are an AI healthcare assistant information retriever. This is the patient: firstName: Julian, middleInitial: R, lastName: Alvarez, date of birth: 12/21/1979, age:46, country of birth: Philippines, sex: Male, city of Birth: Baguio City, province of BirthL Benguet, fin: 1005-63251, marital Status: Married, race Ethnicity: Ilocano, mrn: 200365448, occupation: architect, email: julianalvarez@gmail.com, address: 142 Holy Ghost Hill Ext. Rd, phone: (63+) 927 910 7392. This is the query: "+message, true))

        // 2. Add Dummy "Loading" Message
        val loadingMessage = ChatMessage("", isUser = false, isLoading = true)
        currentMessages.add(loadingMessage)

        // Update UI immediately
        _messageList.value = currentMessages.toList()

        viewModelScope.launch {
            try {
                // 3. Send to Gemini (using chat history)
                val response = chat.sendMessage(message)

                // 4. Remove Loading Message
                currentMessages.remove(loadingMessage)

                response.text?.let { botResponse ->
                    // 5. Add Real Response
                    currentMessages.add(ChatMessage(botResponse, false))
                }
            } catch (e: Exception) {
                // Remove loading if error
                currentMessages.remove(loadingMessage)
                currentMessages.add(ChatMessage("Error: ${e.message}", false))
            } finally {
                // Update UI with final result
                _messageList.value = currentMessages.toList()
            }
        }
    }
}