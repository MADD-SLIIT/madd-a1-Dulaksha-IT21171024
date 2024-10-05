package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Add logic to fetch and display notifications here
        // You can dynamically add notifications to the notificationsContainer

        // Set up bottom navigation functionality
        setupBottomNavigation()
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
            // Stay on the Notifications page as it's the current activity
        }

        navProfile.setOnClickListener {
            // Navigate to the Profile page
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
