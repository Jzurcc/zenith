package com.cc17.zenith

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // --- MARGIN FIX ---
        val paddingDp = 24
        val density = resources.displayMetrics.density
        val paddingPx = (paddingDp * density).toInt()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + paddingPx,
                systemBars.top + paddingPx,
                systemBars.right + paddingPx,
                systemBars.bottom + paddingPx
            )
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val tvEmailError = findViewById<TextView>(R.id.tvEmailError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val tvConfirmError = findViewById<TextView>(R.id.tvConfirmError)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            tvEmailError.visibility = View.GONE
            tvPasswordError.visibility = View.GONE
            tvConfirmError.visibility = View.GONE

            var isValid = true

            // 1. Email Validation (Regex based)
            if (!email.isValidEmail()) {
                tvEmailError.visibility = View.VISIBLE
                isValid = false
            }

            // 2. Password Length Validation (Min 5)
            if (password.length < 5) {
                tvPasswordError.visibility = View.VISIBLE
                isValid = false
            }

            // 3. Confirm Password Validation (Must Match)
            if (password != confirmPassword) {
                tvConfirmError.visibility = View.VISIBLE
                isValid = false
            }

            if (isValid) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
                applyEnterAnimation()
            }
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
            applyExitAnimation()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                applyExitAnimation()
            }
        })
    }

    // Extension function for Email Validation
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun applyEnterAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun applyExitAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}