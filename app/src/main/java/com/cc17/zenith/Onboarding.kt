package com.cc17.zenith

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.os.Build
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class Onboarding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnCreateAccount = findViewById<MaterialButton>(R.id.btnCreateAccount)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnCreateAccount.setOnClickListener {
            navigateToRegister()
        }

        tvLoginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)

        // Check if the phone is running Android 14 (API 34) or newer
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)

        // Check if the phone is running Android 14 (API 34) or newer
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}