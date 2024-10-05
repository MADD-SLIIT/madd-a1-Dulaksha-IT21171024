package com.example.knowledgehubfinal

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView

class QuizActivity : AppCompatActivity() {

    private var currentQuestion = 0
    private val totalQuestions = 15

    // Mixed questions without categorization
    private val questions = listOf(
        "What is the factor of 12?", "What is the slope of a graph?", "What is the factor of 24?",
        "What is the intercept in a graph?", "What is the factor of 18?", "What is a bar graph?",
        "What is the x-axis of a graph?", "What is the range of a graph?", "What is the factor of 36?",
        "What does a pie chart represent?", "What is the median in a graph?", "What is the factor of 48?",
        "What does the gradient of a graph represent?", "What is the mode in a graph?", "What is the factor of 60?"
    )

    // Corresponding options for each question
    private val options = listOf(
        listOf("1", "2", "3", "4"), listOf("Rate of change", "Position", "Intercept", "Constant"),
        listOf("2", "3", "4", "6"), listOf("Where the graph crosses the y-axis", "Slope", "Rate of change", "None"),
        listOf("2", "3", "6", "9"), listOf("A graph with bars", "A pie chart", "A line graph", "None"),
        listOf("Horizontal line", "Vertical line", "Both", "None"), listOf("Difference between values", "Maximum value", "Minimum value", "Both"),
        listOf("4", "6", "9", "12"), listOf("Distribution of data", "Comparison of data", "Trends over time", "None"),
        listOf("Middle value", "Range", "Difference", "Mode"), listOf("4", "6", "8", "12"),
        listOf("Steepness", "Direction", "Position", "None"), listOf("Most frequent value", "Average", "Median", "None"),
        listOf("5", "10", "12", "15")
    )

    // Correct answers for each question
    private val correctAnswers = listOf(
        "3", "Rate of change", "6", "Where the graph crosses the y-axis", "9", "A graph with bars",
        "Horizontal line", "Difference between values", "12", "Distribution of data", "Middle value", "12",
        "Steepness", "Most frequent value", "15"
    )

    private val selectedAnswers = mutableListOf<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        selectedAnswers.addAll(List(totalQuestions) { null })

        // Set the quiz heading dynamically
        val tvQuizHeading: TextView = findViewById(R.id.quizTitle)
        tvQuizHeading.text = "Mathematics Quiz"

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

        // Clear previous selections and set the saved answer if available
        radioGroupOptions.clearCheck()
        val selectedAnswer = selectedAnswers[currentQuestion]
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
        var score = 0

        for (i in 0 until totalQuestions) {
            if (selectedAnswers[i] == correctAnswers[i]) {
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
        btnNext.isVisible = false

        // Show the result in the card view
        val cardResult: MaterialCardView = findViewById(R.id.cardResult)
        val tvResult: TextView = findViewById(R.id.tvResult)
        val tvMessage: TextView = findViewById(R.id.tvMessage)
        val btnFinish: Button = findViewById(R.id.btnFinish)

        cardResult.isVisible = true
        tvResult.text = "$score/$totalQuestions"

        tvMessage.text = when {
            score == totalQuestions -> "Excellent! You got all answers correct."
            score >= totalQuestions / 2 -> "Great job! You passed the quiz."
            else -> "Better luck next time."
        }

        btnFinish.isVisible = true
        btnFinish.setOnClickListener {
            finish()
        }
    }
}
