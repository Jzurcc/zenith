package com.cc17.zenith

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel

class Chatbot : AppCompatActivity() {

    // --- Declare views ---
    private lateinit var backButton: ImageButton
    private lateinit var sendButton: ImageButton
    private lateinit var chatInput: TextInputEditText
    private val chatViewModel: ChatViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContentView(R.layout.activity_chatbot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,

                // Use the larger of the two bottom insets
                systemBars.bottom.coerceAtLeast(ime.bottom)
            )
            insets
        }

        backButton = findViewById(R.id.back)
        sendButton = findViewById(R.id.send_button)
        chatInput = findViewById(R.id.chat_input_edittext)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        sendButton.setOnClickListener {
            handleSendAction()
        }

        // --- Keyboard "Send" action listener ---
        chatInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun handleSendAction() {
        val message = chatInput.text.toString()

        if (message.isNotBlank()) {
            chatViewModel.sendMessage(message)
            chatInput.text?.clear()
        }
    }
}