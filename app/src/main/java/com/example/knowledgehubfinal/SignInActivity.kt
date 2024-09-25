package com.example.knowledgehubfinal
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class SignInActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_in) // Make sure this layout file exists
//    }
//}

// Import necessary packages
// Import necessary packages
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Find the TextView by ID for Sign Up navigation
        val signUpTextView = findViewById<TextView>(R.id.signUpTxt)

        // Set an OnClickListener
        signUpTextView.setOnClickListener {
            // Navigate to the SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Find the sign-in button by ID
        val signInButton = findViewById<Button>(R.id.signInButton)

        // Set an OnClickListener for the sign-in button
        signInButton.setOnClickListener {
            // Navigate to the HomeActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}

