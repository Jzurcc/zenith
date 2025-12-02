package com.cc17.zenith

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
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

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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
        val tvEmailError = findViewById<TextView>(R.id.tvEmailError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGoogle = findViewById<ImageButton>(R.id.btnGoogle)

        // General Error Views
        val layoutGeneralError = findViewById<LinearLayout>(R.id.layoutGeneralError)
        val tvGeneralErrorText = findViewById<TextView>(R.id.tvGeneralErrorText)

        // Google Sign In Launcher
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
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

        // Handle Email Login Click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reset Errors
            tvEmailError.visibility = View.GONE
            tvPasswordError.visibility = View.GONE
            layoutGeneralError.visibility = View.GONE

            var isValid = true

            if (!email.isValidEmail()) {
                tvEmailError.visibility = View.VISIBLE
                isValid = false
            }

            if (password.length < 5) {
                tvPasswordError.visibility = View.VISIBLE
                isValid = false
            }

            if (isValid) {
                // Firebase Login
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            navigateToMain()
                        } else {
                            // Show error in layout instead of Toast
                            Log.e("Login", "Authentication failed", task.exception)
                            tvGeneralErrorText.text = "Authentication failed: ${task.exception?.message}"
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
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun applyExitAnimation() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}