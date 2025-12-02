package com.cc17.zenith

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.activity.OnBackPressedCallback

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var mainTitle: TextView
    private lateinit var mainSubtitle: TextView

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        // Force light mode and portrait orientation
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                else {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout)

                    if (currentFragment is Dashboard) {
                        finish()
                    } else {
                        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
                        bottomNavigationView.selectedItemId = R.id.home
                    }
                }
            }
        })

        val imageButton: Button = findViewById(R.id.qr_code)
        imageButton.setOnClickListener {
            val intent = Intent(this, QRScanner::class.java)
            startActivity(intent)
        }

        mainTitle = findViewById(R.id.toolbar_title)
        mainSubtitle = findViewById(R.id.toolbar_subtitle)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        sharedViewModel.title.observe(this) { newTitle ->
            mainTitle.text = newTitle
        }

        sharedViewModel.subtitle.observe(this) { newSubtitle ->
            mainSubtitle.text = newSubtitle
        }

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Initialize Toolbar + Drawer + NavigationView
        val toolbarIcon: ImageButton = findViewById(R.id.toolbar_icon)


        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_view)


        toolbarIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, Dashboard())
                .commit()
            navigationView.setCheckedItem(R.id.home)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            // Check if we are interacting with the Chatbot item
            val isChatbot = item.itemId == R.id.ehr

            val switchAction = Runnable {
                if (!isChatbot) {
                    // Only clear stack and replace fragments if NOT opening Chatbot
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    when (item.itemId) {
                        R.id.home -> replaceFragment(Dashboard())
                        R.id.disease -> replaceFragment(diseasetrends())
                        R.id.profile -> replaceFragment(patients())
                    }

                    navigationView.menu.setGroupCheckable(0, true, true)
                } else {
                    toChatbot()
                }
            }

            if (!checkUnsavedChanges(switchAction)) {
                return@setOnItemSelectedListener false
            }
            !isChatbot
        }


        navigationView.setNavigationItemSelectedListener { menuItem ->
            val switchAction = Runnable {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                when (menuItem.itemId) {
                    R.id.home -> replaceFragment(Dashboard())
                    R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
                }

                // Sync Bottom Nav
                bottomNavigationView.menu.setGroupCheckable(0, true, false)
                for (i in 0 until bottomNavigationView.menu.size()) {
                    bottomNavigationView.menu.getItem(i).isChecked = false
                }
                bottomNavigationView.menu.setGroupCheckable(0, true, true)
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            if (!checkUnsavedChanges(switchAction)) {
                return@setNavigationItemSelectedListener false
            }

            true
        }

        // Handle Intent from QR
        if (intent.hasExtra("scanned_patient_json")) {
            val jsonString = intent.getStringExtra("scanned_patient_json")
            sharedViewModel.initializeDefaultPatients(this)
            openPatientInfoWithData(jsonString)
        }
    }

    private fun checkUnsavedChanges(onConfirmAction: Runnable): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout)

        if (currentFragment is OnUnsavedChangesListener) {
            if (currentFragment.hasUnsavedChanges()) {
                // Show dialog, passing the action to run if they click "Discard"
                currentFragment.showUnsavedChangesDialog(onConfirmAction)
                return false // Don't switch yet
            }
        }

        // Safe to switch immediately
        onConfirmAction.run()
        return true
    }

    // if the QR activity was already open in background
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent.hasExtra("scanned_patient_json")) {
            val jsonString = intent.getStringExtra("scanned_patient_json")

            // load dummy data here too
            sharedViewModel.initializeDefaultPatients(this)

            openPatientInfoWithData(jsonString)
        }
    }


    private fun toChatbot() {
        val intent = Intent(this, Chatbot::class.java)
        startActivity(intent)
    }

    private fun toOCR() {
        val intent = Intent(this, QRScanner::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

    private fun openPatientInfoWithData(jsonString: String?) {
        // 1. Create the fragment instance
        val patientInfoFragment = PatientInfo()

        // 2. Create the Bundle to pass the data
        val args = Bundle()
        args.putString("qr_json_data", jsonString)
        patientInfoFragment.arguments = args

        // 3. Replace the current fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, patientInfoFragment)
            .addToBackStack(null) // Optional: Allows user to press Back button to return to Dashboard
            .commit()
    }
}
