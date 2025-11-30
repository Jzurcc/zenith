package com.cc17.zenith

import android.content.Intent
import android.os.Bundle
import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.google.android.material.button.MaterialButton

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // Intent to go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finishAffinity()

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

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
            applyExitAnimation()
        }

        // Handle the system "Back" button (bottom of phone)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                applyExitAnimation()
            }
        })
    }
    private fun applyExitAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}