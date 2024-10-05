package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.knowledgehubfinal.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    // Define the admin email (you can add more emails to this list if there are multiple admins)
    private val adminEmail = "admin@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set onClick listener for Sign In button
        binding.signInButton.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.pwdEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set onClick listener for Sign Up TextView (if the user doesn't have an account)
        binding.signUpTxt.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // Method to sign in the user with Firebase Authentication
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null && currentUser.email != null) {
                        val userEmail = currentUser.email!!.toLowerCase() // Convert to lowercase for case-insensitive comparison
                        if (userEmail == adminEmail.toLowerCase()) {
                            // If the email matches the admin email, redirect to Admin Dashboard
                            val intent = Intent(this@SignInActivity, DashboardActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Otherwise, redirect to User Dashboard
                            val intent = Intent(this@SignInActivity, UserDashboardActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        // Handle the case where the email is null or currentUser is null
                        Toast.makeText(this, "Failed to retrieve user email", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Show error message if login fails
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
