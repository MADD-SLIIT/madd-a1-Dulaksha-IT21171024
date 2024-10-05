package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Reference to the container where results will be dynamically added
        val resultsContainer = findViewById<LinearLayout>(R.id.resultsContainer)

        // List of quiz results (You can replace this with real data)
        val quizResults = listOf(
            QuizResult("Math Quiz", 85, "Great job! Keep it up!"),
            QuizResult("Science Quiz", 90, "Excellent!"),
            QuizResult("Mathematics Quiz", 75, "Good effort! Improve on the weaker areas.")
        )

        // Dynamically adding quiz result cards
        quizResults.forEach { result ->
            addQuizResultCard(result, resultsContainer)
        }

        // Set up bottom navigation functionality
        setupBottomNavigation()
    }

    // Function to dynamically add a quiz result card
    private fun addQuizResultCard(result: QuizResult, container: LinearLayout) {
        // Find the card template
        val cardTemplate = findViewById<MaterialCardView>(R.id.cardQuizResultTemplate)

        // Clone the template
        val cardView = cardTemplate.clone() as MaterialCardView

        // Make the card view visible
        cardView.visibility = View.VISIBLE

        // Set the quiz title and mark
        val quizTitle = cardView.findViewById<TextView>(R.id.QuizTitle_results)
        quizTitle.text = "${result.quizName} Marks: ${result.quizMarks}%"

        // Set the quiz comment
        val quizComment = cardView.findViewById<TextView>(R.id.QuizComment)
        quizComment.text = result.comment

        // Add the card to the container
        container.addView(cardView)
    }

    // Set up the bottom navigation click handling
    private fun setupBottomNavigation() {
        // Find the bottom navigation icons
        val UserHome = findViewById<ImageView>(R.id.UserHome)
        val navNotifications = findViewById<ImageView>(R.id.navNotifications)
        val navProfile = findViewById<ImageView>(R.id.navProfile)
        val logout = findViewById<ImageView>(R.id.logout)

        // Set click listeners for navigation items
        UserHome.setOnClickListener {
            // Navigate to the Dashboard (or Main/Home activity)
            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
        }

        navNotifications.setOnClickListener {
            // Navigate to the Notifications page
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            // Navigate to the Profile page
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener {
            // Navigate to the Profile page
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Navigate to SignInActivity when logout button is clicked
        findViewById<ImageView>(R.id.logout).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish() // Optional: Closes the current activity so the user can't go back
        }
    }

    // Extension function to clone the card view
    fun View.clone(): View {
        val parent = this.parent as? ViewGroup
        parent?.removeView(this) // Detach from parent to prevent issues
        return this
    }
}

// Data class to represent quiz results
data class QuizResult(val quizName: String, val quizMarks: Int, val comment: String)
