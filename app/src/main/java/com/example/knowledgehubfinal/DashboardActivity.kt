package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnEnrollMath: Button
    private lateinit var btnEnrollScience: Button
    private lateinit var cardMathematics: MaterialCardView
    private lateinit var cardScience: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btnEnrollMath = findViewById(R.id.btnEnroll)
        btnEnrollScience = findViewById(R.id.btnEnrollScience)
        cardMathematics = findViewById(R.id.cardMathematics)
        cardScience = findViewById(R.id.cardScience)

        btnEnrollMath.setOnClickListener {
            onEnrollButtonClick(btnEnrollMath, "Mathematics")
        }

        btnEnrollScience.setOnClickListener {
            onEnrollButtonClick(btnEnrollScience, "Science")
        }

        // Set click listeners for the subject cards
        cardMathematics.setOnClickListener {
            navigateToSubject("Mathematics")
        }

        cardScience.setOnClickListener {
            navigateToSubject("Science")
        }
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
}
