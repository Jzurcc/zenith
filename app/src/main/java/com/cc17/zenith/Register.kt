package com.cc17.zenith

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Requires google-services.json
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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

        // Error Layouts (Containers)
        val layoutGeneralError = findViewById<LinearLayout>(R.id.layoutGeneralError) // General Error Layout
        val layoutEmailError = findViewById<LinearLayout>(R.id.layoutEmailError)
        val layoutPasswordError = findViewById<LinearLayout>(R.id.layoutPasswordError)
        val layoutConfirmError = findViewById<LinearLayout>(R.id.layoutConfirmError)

        // Error Texts
        val tvGeneralErrorText = findViewById<TextView>(R.id.tvGeneralErrorText) // General Error Text
        val tvEmailError = findViewById<TextView>(R.id.tvEmailErrorText)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnGoogle = findViewById<ImageButton>(R.id.btnGoogle) // Changed to ImageButton

        // Google Sign In Launcher
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    tvGeneralErrorText.text = "Google sign in failed: ${e.message}"
                    layoutGeneralError.visibility = View.VISIBLE
                }
            }
        }

        // Handle Google Button Click
        btnGoogle.setOnClickListener {
            layoutGeneralError.visibility = View.GONE
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Reset Errors
            layoutGeneralError.visibility = View.GONE
            layoutEmailError.visibility = View.GONE
            layoutPasswordError.visibility = View.GONE
            layoutConfirmError.visibility = View.GONE

            var isValid = true

            // 1. Email Validation (Regex based)
            if (!email.isValidEmail()) {
                tvEmailError.text = "Invalid email. Must contain a domain"
                layoutEmailError.visibility = View.VISIBLE
                isValid = false
            }

            // 2. Password Length Validation (Min 5)
            if (password.length < 5) {
                layoutPasswordError.visibility = View.VISIBLE
                isValid = false
            }

            // 3. Confirm Password Validation (Must Match)
            if (password != confirmPassword) {
                layoutConfirmError.visibility = View.VISIBLE
                isValid = false
            }

            if (isValid) {
                // Create user in Firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            navigateToMain()
                        } else {
                            // Display error in the general error disclaimer at the top
                            tvGeneralErrorText.text = "Registration failed: ${task.exception?.message}"
                            layoutGeneralError.visibility = View.VISIBLE
                        }
                    }
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    val tvGeneralErrorText = findViewById<TextView>(R.id.tvGeneralErrorText)
                    val layoutGeneralError = findViewById<LinearLayout>(R.id.layoutGeneralError)
                    tvGeneralErrorText.text = "Authentication Failed."
                    layoutGeneralError.visibility = View.VISIBLE
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
        applyEnterAnimation()
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