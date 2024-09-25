package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SubjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        // Find the quiz button for Factors
        val quizFactorsButton: Button = findViewById(R.id.btnQuizFactors)
        quizFactorsButton.setOnClickListener {
            // Navigate to QuizActivity with the topic name "Factors"
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("topic", "Factors")
            startActivity(intent)
        }

        // Find the quiz button for Graphs
        val quizGraphsButton: Button = findViewById(R.id.btnQuizGraphs)
        quizGraphsButton.setOnClickListener {
            // Navigate to QuizActivity with the topic name "Graphs"
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("topic", "Graphs")
            startActivity(intent)
        }
    }
}

