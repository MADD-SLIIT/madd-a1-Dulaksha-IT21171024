package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var editButton: Button
    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etAge: EditText
    private lateinit var etSchool: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        editButton = findViewById(R.id.btnEditProfile)
        etName = findViewById(R.id.etName)
        etAddress = findViewById(R.id.etAddress)
        etAge = findViewById(R.id.etAge)
        etSchool = findViewById(R.id.etSchool)

        // Disable editing initially
        disableEditing()

        editButton.setOnClickListener {
            if (editButton.text.toString() == "Edit") {
                enableEditing()
                editButton.text = "Save"
            } else {
                // Save changes
                disableEditing()
                editButton.text = "Edit"
            }
        }

        // Set up bottom navigation functionality
        setupBottomNavigation()
    }

    // Method to disable editing
    private fun disableEditing() {
        etName.isEnabled = false
        etAddress.isEnabled = false
        etAge.isEnabled = false
        etSchool.isEnabled = false

        etName.setBackgroundResource(android.R.color.transparent)
        etAddress.setBackgroundResource(android.R.color.transparent)
        etAge.setBackgroundResource(android.R.color.transparent)
        etSchool.setBackgroundResource(android.R.color.transparent)
    }

    // Method to enable editing
    private fun enableEditing() {
        etName.isEnabled = true
        etAddress.isEnabled = true
        etAge.isEnabled = true
        etSchool.isEnabled = true

        etName.setBackgroundResource(android.R.drawable.edit_text)
        etAddress.setBackgroundResource(android.R.drawable.edit_text)
        etAge.setBackgroundResource(android.R.drawable.edit_text)
        etSchool.setBackgroundResource(android.R.drawable.edit_text)
    }

    // Set up the bottom navigation click handling
    private fun setupBottomNavigation() {
        // Find the bottom navigation icons
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navNotifications = findViewById<ImageView>(R.id.navNotifications)
        val navProfile = findViewById<ImageView>(R.id.navProfile)

        // Set click listeners for navigation items
        navHome.setOnClickListener {
            // Navigate to the Dashboard (or Main/Home activity)
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        navNotifications.setOnClickListener {
            // Navigate to the Notifications page
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            // Navigate to the Profile page (which is the current activity)
            // No need to start a new activity as we're already in ProfileActivity
        }
    }
}
