package com.cc17.zenith

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

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
            // Your code here
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, Dashboard())
                .addToBackStack(null)
                .commit()
            navigationView.setCheckedItem(R.id.home)
        }

        replaceFragment(Dashboard())
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Dashboard())
                R.id.disease -> replaceFragment(diseasetrends())
                R.id.ehr -> replaceFragment(ehr())
                R.id.profile -> replaceFragment(patients())
            }

            navigationView.menu.setGroupCheckable(0, true, false)
            for (i in 0 until navigationView.menu.size) {
                navigationView.menu[i].isChecked = false
            }

            navigationView.menu.setGroupCheckable(0, true, true)
            true
        }

        // TODO: Add settings and edit profile
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> replaceFragment(Dashboard())
                R.id.appointments -> replaceFragment(Appointments())
                R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            }

            bottomNavigationView.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNavigationView.menu.size) {
                bottomNavigationView.menu[i].isChecked = false
            }

            bottomNavigationView.menu.setGroupCheckable(0, true, true)

            drawerLayout.closeDrawer(GravityCompat.START)
            true
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
            .addToBackStack(null)
            .commit()
    }
}
