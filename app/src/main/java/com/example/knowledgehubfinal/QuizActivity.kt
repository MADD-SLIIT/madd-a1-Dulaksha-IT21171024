package com.example.knowledgehubfinal

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView

class QuizActivity : AppCompatActivity() {

    private var currentQuestion = 0
    private val totalQuestions = 3
    private val questionsMap = mapOf(
        "Factors" to listOf("What is the factor of 12?", "What is the factor of 24?", "What is the factor of 18?"),
        "Graphs" to listOf("What is a line graph?", "What is the slope of a graph?", "What is the intercept in a graph?")
    )

    private val optionsMap = mapOf(
        "Factors" to listOf(
            listOf("1", "2", "3", "4"),
            listOf("2", "3", "4", "6"),
            listOf("2", "3", "6", "9")
        ),
        "Graphs" to listOf(
            listOf("A plot of points", "A bar chart", "A pie chart", "None"),
            listOf("Rate of change", "Position", "Intercept", "Constant"),
            listOf("Where the graph crosses the y-axis", "Slope", "Rate of change", "None")
        )
    )

    private val correctAnswers = mapOf(
        "Factors" to listOf("3", "6", "9"),
        "Graphs" to listOf("A plot of points", "Rate of change", "Where the graph crosses the y-axis")
    )

    private lateinit var selectedTopic: String
    private val selectedAnswers = mutableListOf<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        selectedTopic = intent.getStringExtra("topic") ?: "Factors"

        // Initialize selected answers with null values
        selectedAnswers.addAll(List(totalQuestions) { null })

        // Set the quiz heading dynamically
        val tvQuizHeading: TextView = findViewById(R.id.quizTitle)
        tvQuizHeading.text = "$selectedTopic Quiz"

        loadQuestion()

        val btnNext: Button = findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            saveSelectedAnswer()

            if (currentQuestion < totalQuestions - 1) {
                currentQuestion++
                loadQuestion()
            } else {
                submitQuiz()
            }
        }
    }

    private fun loadQuestion() {
        val tvQuestion: TextView = findViewById(R.id.Question)
        val tvProgress: TextView = findViewById(R.id.Progress_indicator)
        val radioGroupOptions: RadioGroup = findViewById(R.id.radioGroupOptions)

        val questions = questionsMap[selectedTopic] ?: listOf("No questions available")
        val options = optionsMap[selectedTopic] ?: listOf(listOf("No options available"))

        tvQuestion.text = questions[currentQuestion]
        tvProgress.text = "Question ${currentQuestion + 1} of $totalQuestions"

        val radioOption1: RadioButton = findViewById(R.id.radioOption1)
        val radioOption2: RadioButton = findViewById(R.id.radioOption2)
        val radioOption3: RadioButton = findViewById(R.id.radioOption3)
        val radioOption4: RadioButton = findViewById(R.id.radioOption4)

        val currentOptions = options[currentQuestion]
        radioOption1.text = currentOptions[0]
        radioOption2.text = currentOptions[1]
        radioOption3.text = currentOptions[2]
        radioOption4.text = currentOptions[3]

        val selectedAnswer = selectedAnswers[currentQuestion]
        radioGroupOptions.clearCheck()
        when (selectedAnswer) {
            radioOption1.text -> radioOption1.isChecked = true
            radioOption2.text -> radioOption2.isChecked = true
            radioOption3.text -> radioOption3.isChecked = true
            radioOption4.text -> radioOption4.isChecked = true
        }

        val btnNext: Button = findViewById(R.id.btnNext)
        btnNext.text = if (currentQuestion == totalQuestions - 1) "Submit" else "Next"
    }

    private fun saveSelectedAnswer() {
        val radioGroupOptions: RadioGroup = findViewById(R.id.radioGroupOptions)
        val selectedRadioButtonId = radioGroupOptions.checkedRadioButtonId

        val selectedRadioButton: RadioButton? = findViewById(selectedRadioButtonId)
        selectedAnswers[currentQuestion] = selectedRadioButton?.text?.toString()
    }

    private fun submitQuiz() {
        val correctAnswersList = correctAnswers[selectedTopic] ?: listOf()
        var score = 0

        for (i in 0 until totalQuestions) {
            if (selectedAnswers[i] == correctAnswersList[i]) {
                score++
            }
        }

        showResult(score)
    }

    private fun showResult(score: Int) {
        val tvQuestion: TextView = findViewById(R.id.Question)
        val tvProgress: TextView = findViewById(R.id.Progress_indicator)
        val radioGroupOptions: RadioGroup = findViewById(R.id.radioGroupOptions)
        val btnNext: Button = findViewById(R.id.btnNext)

        tvQuestion.isVisible = false
        tvProgress.isVisible = false
        radioGroupOptions.isVisible = false

        // Hide the next button
        btnNext.isVisible = false

        // Show the result in a card view
        val cardResult: MaterialCardView = findViewById(R.id.cardResult)
        val tvResult: TextView = findViewById(R.id.tvResult)
        val tvMessage: TextView = findViewById(R.id.tvMessage)

        tvResult.text = "You scored $score out of $totalQuestions!"
        tvMessage.text = when (score) {
            totalQuestions -> "Excellent! You've mastered this topic."
            in 1 until totalQuestions -> "Great effort! Keep practicing."
            else -> "Don't worry! Try again and improve."
        }

        cardResult.isVisible = true

        // Add a "Finish" button to go back
        btnNext.text = "Finish"
        btnNext.isVisible = true
        btnNext.setOnClickListener {
            finish()  // This will close the QuizActivity and navigate back to the previous activity
        }
    }

}
