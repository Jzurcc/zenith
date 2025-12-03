package com.cc17.zenith

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Chatbot : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var btnSelectPatient: ImageButton
    private lateinit var sendButton: ImageButton
    private lateinit var chatInput: TextInputEditText
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    // Tip Buttons
    private lateinit var patient_records: Button
    private lateinit var prescription_stats: Button
    private lateinit var follow_ups: Button
    private lateinit var referral_suggestions: Button

    private val chatViewModel: ChatViewModel by viewModels()

    // Store the passed patient list
    private var allPatients: ArrayList<Patient>? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbot)

        // Receive the patient list from MainActivity
        allPatients = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("all_patients", Patient::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableArrayListExtra("all_patients")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom.coerceAtLeast(ime.bottom)
            )
            insets
        }

        initViews()
        setupListeners()
        setupRecyclerView()

        chatViewModel.messageList.observe(this) { newList ->
            chatAdapter.updateMessages(newList)
            if (newList.isNotEmpty()) {
                chatRecyclerView.scrollToPosition(newList.size - 1)
            }
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back)
        btnSelectPatient = findViewById(R.id.btn_select_patient)
        sendButton = findViewById(R.id.send_button)
        chatInput = findViewById(R.id.chat_input_edittext)
        chatRecyclerView = findViewById(R.id.chat_recyclerview)

        patient_records = findViewById(R.id.patient_records)
        prescription_stats = findViewById(R.id.prescription_stats)
        follow_ups = findViewById(R.id.follow_ups)
        referral_suggestions = findViewById(R.id.referral_suggestions)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish() // Use finish() instead of new Intent to preserve stack
        }

        // --- NEW: Select Patient Context ---
        btnSelectPatient.setOnClickListener {
            showPatientSelectionDialog()
        }

        sendButton.setOnClickListener { handleSendAction() }

        chatInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        // Tip Buttons
        patient_records.setOnClickListener { chatInput.setText("Find patient record ") }
        prescription_stats.setOnClickListener { chatInput.setText("What are the prescription stats of ") }
        follow_ups.setOnClickListener { chatInput.setText("Provide upcoming follow-ups") }
        referral_suggestions.setOnClickListener { chatInput.setText("Provide referral suggestions for ") }
    }

    private fun showPatientSelectionDialog() {
        if (allPatients.isNullOrEmpty()) {
            Toast.makeText(this, "No patients found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create list of names for the dialog
        val names = allPatients!!.map { "${it.lastName}, ${it.firstName}" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Patient Context")
            .setItems(names) { _, which ->
                // User clicked on a patient
                val selectedPatient = allPatients!![which]

                // Update ViewModel context
                chatViewModel.setPatientContext(selectedPatient)

                // Visual confirmation logic is handled inside ViewModel updating the list
            }
            .setNegativeButton("Cancel", null)
            .show()
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