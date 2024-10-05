package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var btnEnrollMath: Button
    private lateinit var btnEnrollScience: Button
    private lateinit var cardMathematics: MaterialCardView
    private lateinit var cardScience: MaterialCardView
    private lateinit var searchBar: EditText
    private lateinit var navNotifications: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var quizbtn: Button
    private lateinit var UserHome: ImageView
    private lateinit var logout: ImageView // Initialize logout button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdashboard)

//        // Find the quizMaths button by its ID
//        val quizMathsButton: Button = findViewById(R.id.quizMaths)
//
//        // Set an OnClickListener on the quizMaths button
//        quizMathsButton.setOnClickListener {
//            // Create an intent to navigate to QuizActivity
//            val intent = Intent(this@UserDashboardActivity, QuizActivity::class.java)
//            // Start the QuizActivity
//            startActivity(intent)
//        }

        btnEnrollMath = findViewById(R.id.UbtnEnroll)
        btnEnrollScience = findViewById(R.id.UbtnEnrollScience)
        cardMathematics = findViewById(R.id.cardMathematics)
        cardScience = findViewById(R.id.cardScience)
        searchBar = findViewById(R.id.searchBar) // Initialize search bar
        navNotifications = findViewById(R.id.navNotifications) // Initialize notifications icon
        navProfile = findViewById(R.id.navProfile); // Initialize profile icon
        quizbtn = findViewById(R.id.quizMaths)
        UserHome = findViewById(R.id.UserHome)
        logout = findViewById(R.id.logout) // Initialize logout button


        btnEnrollMath.setOnClickListener {
            onEnrollButtonClick(btnEnrollMath, "Mathematics")
        }

//        btnEnrollScience.setOnClickListener {
//            onEnrollButtonClick(btnEnrollScience, "Science")
//        }

        // Set click listeners for the subject cards
        cardMathematics.setOnClickListener {
            navigateToSubject("Mathematics")
        }
//
//        cardScience.setOnClickListener {
//            navigateToSubject("Science")
//        }

        // Set up navigation for bottom icons
        navNotifications.setOnClickListener {
            navigateToNotifications()
        }

        navProfile.setOnClickListener {
            navigateToProfile()
        }

        quizbtn.setOnClickListener {
            navigateToQuiz()
        }

        UserHome.setOnClickListener {
            navigateToUserDasboard()
        }

        logout.setOnClickListener {
            navigateToSignIn() // Set up navigation to SignInActivity
        }
    }

    private fun navigateToQuiz() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }

    private fun onEnrollButtonClick(button: Button, subject: String) {
        // Change button text and disable it
        button.text = "Enrolled"
        button.isEnabled = false
        button.setBackgroundColor(resources.getColor(R.color.gray)) // Change to your desired disabled color

        // Navigate to SubjectActivity
        navigateToSubject(subject)
    }

    private fun navigateToSubject(subject: String) {
        val intent = Intent(this, SubjectActivity::class.java)
        intent.putExtra("SUBJECT_TITLE", subject)
        startActivity(intent)
    }

    private fun navigateToNotifications() {
        val intent = Intent(this, ResultsActivity::class.java) // Replace with your actual Notifications Activity
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java) // Replace with your actual Profile Activity
        startActivity(intent)
    }

    private fun navigateToUserDasboard() {
        val intent = Intent(this, UserDashboardActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}