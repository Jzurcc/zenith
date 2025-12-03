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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.activity.OnBackPressedCallback
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var mainTitle: TextView
    private lateinit var mainSubtitle: TextView

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

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
                } else {
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

        // --- FIX: Initialize Data Immediately ---
        // This ensures the list exists even if you haven't visited the Profile tab yet.
        sharedViewModel.initializeDefaultPatients(this)

        sharedViewModel.title.observe(this) { newTitle ->
            mainTitle.text = newTitle
        }

        sharedViewModel.subtitle.observe(this) { newSubtitle ->
            mainSubtitle.text = newSubtitle
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
            val isChatbot = item.itemId == R.id.ehr

            val switchAction = Runnable {
                if (!isChatbot) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    when (item.itemId) {
                        R.id.home -> replaceFragment(Dashboard())
                        R.id.disease -> replaceFragment(diseasetrends())
                        R.id.profile -> replaceFragment(patients())
                    }

                    navigationView.menu.setGroupCheckable(0, true, false)
                    for (i in 0 until navigationView.menu.size()) {
                        navigationView.menu.getItem(i).isChecked = false
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

        if (intent.hasExtra("scanned_patient_json")) {
            val jsonString = intent.getStringExtra("scanned_patient_json")
            // Data is already init above, so just open the info
            openPatientInfoWithData(jsonString)
        }
    }

    private fun checkUnsavedChanges(onConfirmAction: Runnable): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout)

        if (currentFragment is OnUnsavedChangesListener) {
            if (currentFragment.hasUnsavedChanges()) {
                currentFragment.showUnsavedChangesDialog(onConfirmAction)
                return false
            }
        }
        onConfirmAction.run()
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent.hasExtra("scanned_patient_json")) {
            val jsonString = intent.getStringExtra("scanned_patient_json")
            sharedViewModel.initializeDefaultPatients(this)
            openPatientInfoWithData(jsonString)
        }
    }

    private fun toChatbot() {
        val intent = Intent(this, Chatbot::class.java)

        // Pass the FULL patient list to the Chatbot
        val allPatients = sharedViewModel.getPatientList().value
        if (allPatients != null) {
            intent.putParcelableArrayListExtra("all_patients", ArrayList(allPatients))
        }

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
        val patientInfoFragment = PatientInfo()
        val args = Bundle()
        args.putString("qr_json_data", jsonString)
        patientInfoFragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, patientInfoFragment)
            .addToBackStack(null)
            .commit()
    }
}