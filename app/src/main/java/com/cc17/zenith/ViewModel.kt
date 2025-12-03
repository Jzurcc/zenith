package com.cc17.zenith

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    // Helper to create model with specific context
    private fun createModel(systemContext: String): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                "gemini-2.5-flash",
                systemInstruction = content { text(systemContext)
                }
            )

    }

    // Default: General Assistant (No specific patient yet)
    private var generativeModel = createModel("You are the AI Healthcare Assistant of ZENITH. Your task is information retrieval ONLY. Use Markdown.")

    // The active chat session
    private var chat: Chat = generativeModel.startChat()

    private val _messageList = MutableLiveData<List<ChatMessage>>()
    val messageList: LiveData<List<ChatMessage>> get() = _messageList

    private val currentMessages = mutableListOf<ChatMessage>()

    // --- NEW: Function to switch patient context ---
    fun setPatientContext(patient: Patient) {
        // 1. Build the Context String (RAG)
        val patientInfo = """
            You are now analyzing the medical record of:
            Name: ${patient.firstName} ${patient.lastName}
            MRN: ${patient.mrn}
            DOB: ${patient.dob} (${patient.age} yrs)
            Sex: ${patient.sex}
            Address: ${patient.address1}, ${patient.city}
            Contact: ${patient.primaryPhoneNumber}
            Email: ${patient.email}
            Remarks/Notes: ${patient.remarks}
            
            Key Medical Data:
            - Allergies: ${if (patient.allergies.isNotEmpty()) patient.allergies.joinToString() else "None recorded"}
            - Organ Donor: ${if (patient.isOrganDonor) "Yes" else "No"}
            
            Instructions:
            1. Answer questions strictly based on this patient's data.
            2. If asked about something not in this record, state that you don't have that information.
            3. Be professional and concise.
        """.trimIndent()

        // 2. Re-initialize the model with the NEW instruction
        generativeModel = createModel(patientInfo)

        // 3. Restart the chat session (wipes previous memory to avoid confusion between patients)
        chat = generativeModel.startChat()

        // 4. Add a visual system message so the user knows context has changed
        currentMessages.add(ChatMessage("Context switched to patient: ${patient.lastName}, ${patient.firstName}", false))
        _messageList.value = currentMessages.toList()
    }

    fun sendMessage(message: String) {
        // 1. Add User Message
        currentMessages.add(ChatMessage(message, true))

        // 2. Add Dummy "Loading" Message
        val loadingMessage = ChatMessage("", isUser = false, isLoading = true)
        currentMessages.add(loadingMessage)

        // Update UI
        _messageList.value = currentMessages.toList()

        viewModelScope.launch {
            try {
                // 3. Send to Gemini (using the CURRENT active chat session)
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