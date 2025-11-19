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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Chatbot : AppCompatActivity() {

    // --- Declare views ---
    private lateinit var backButton: ImageButton
    private lateinit var sendButton: ImageButton
    private lateinit var chatInput: TextInputEditText
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var patient_records: Button
    private lateinit var prescription_stats: Button
    private lateinit var follow_ups: Button
    private lateinit var referral_suggestions: Button
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
        chatRecyclerView = findViewById(R.id.chat_recyclerview)
        setupRecyclerView()

        // tip buttons
        patient_records = findViewById(R.id.patient_records)
        prescription_stats = findViewById(R.id.prescription_stats)
        follow_ups = findViewById(R.id.follow_ups)
        referral_suggestions = findViewById(R.id.referral_suggestions)

        patient_records.setOnClickListener {
            chatInput.setText("Find patient record ")
        }

        prescription_stats.setOnClickListener {
            chatInput.setText("What are the prescription stats of ")
        }

        follow_ups.setOnClickListener {
            chatInput.setText("Provide upcoming follow-ups")
        }

        referral_suggestions.setOnClickListener {
            chatInput.setText("Provide referral suggestions for ")
        }

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

        chatViewModel.messageList.observe(this) { newList ->
            // This code runs every time the message list in ViewModel changes.

            // 1. Update the adapter's data
            chatAdapter.updateMessages(newList)

            // 2. Scroll to the bottom to show the new message
            chatRecyclerView.scrollToPosition(newList.size - 1)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(emptyList())

        val layoutManager = LinearLayoutManager(this)

        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = layoutManager
    }

    private fun handleSendAction() {
        val message = chatInput.text.toString()

        if (message.isNotBlank()) {
            chatViewModel.sendMessage(message)
            chatInput.text?.clear()
        }
    }
}