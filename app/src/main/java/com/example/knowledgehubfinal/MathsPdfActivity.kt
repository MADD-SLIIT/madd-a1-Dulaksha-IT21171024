package com.example.knowledgehubfinal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.knowledgehubfinal.databinding.ActivityMathsPdfBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class MathsPdfActivity : AppCompatActivity(), PdfFileAdaptorActivity.PdfClickListener {

    private lateinit var binding: ActivityMathsPdfBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFileAdaptorActivity
    private val pdfList = mutableListOf<PdfFile>()

    private lateinit var navHome: ImageView // Initialize navHome button
    private lateinit var logout: ImageView // Initialize logout button
    private lateinit var navProfile: ImageView // Initialize logout button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMathsPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")

        // Initialize adapter
        adapter = PdfFileAdaptorActivity(pdfList, this)

        // Set up RecyclerView
        binding.MathsPdfRecylerview.apply {
            layoutManager = LinearLayoutManager(this@MathsPdfActivity)
            adapter = this@MathsPdfActivity.adapter
        }

        // Initialize buttons for navigation
        navHome = findViewById(R.id.navHome) // Assuming these ImageViews are in the XML layout
        logout = findViewById(R.id.logout)
        navProfile = findViewById(R.id.navProfile)

        // Set click listeners for navigation buttons
        navHome.setOnClickListener {
            navigateToUserDashboard() // Navigate to UserDashboardActivity
        }

        logout.setOnClickListener {
            navigateToSignIn() // Navigate to SignInActivity
        }

        navProfile.setOnClickListener {
            navigateToProfile() // Navigate to SignInActivity
        }


        // Fetch PDF files from database
        fetchPdfFiles()
    }

    // Fetch PDF files from the Firebase Realtime Database
    private fun fetchPdfFiles() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfList.clear() // Clear the list to avoid duplicates
                for (data in snapshot.children) {
                    val pdfFile = data.getValue(PdfFile::class.java)
                    pdfFile?.let { pdfList.add(it) }
                }
                adapter.submitList(pdfList.toList()) // Update the adapter's data
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MathsPdfActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Rename a PDF file in Firebase and the local list
    private fun renamePdfFile(position: Int, newName: String) {
        val pdfFile = pdfList[position]
        val query = databaseReference.orderByChild("downloadUrl").equalTo(pdfFile.downloadUrl)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        data.ref.child("fileName").setValue(newName)
                            .addOnSuccessListener {
                                // Update the local list
                                pdfList[position].fileName = newName
                                adapter.submitList(pdfList.toList()) // Update the adapter
                                Toast.makeText(this@MathsPdfActivity, "PDF renamed successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@MathsPdfActivity, "Failed to rename PDF", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Delete a PDF file from Firebase and the local list
    private fun deletePdfFile(position: Int) {
        val pdfFile = pdfList[position]
        val query = databaseReference.orderByChild("downloadUrl").equalTo(pdfFile.downloadUrl)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfFile.downloadUrl)
                        storageReference.delete().addOnSuccessListener {
                            data.ref.removeValue().addOnSuccessListener {
                                pdfList.removeAt(position) // Remove from the local list
                                adapter.submitList(pdfList.toList()) // Update the adapter
                                Toast.makeText(this@MathsPdfActivity, "PDF deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Override method to handle PDF deletion
    override fun onPdfDeleted(position: Int) {
        deletePdfFile(position)
    }

    // Override method to handle PDF renaming
    override fun onPdfRename(position: Int, currentName: String) {
        val newNameDialog = AlertDialog.Builder(this)
        newNameDialog.setTitle("Rename PDF")

        val input = EditText(this)
        input.hint = "Enter new name"
        newNameDialog.setView(input)

        newNameDialog.setPositiveButton("Rename") { dialog, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                renamePdfFile(position, newName)
            }
            dialog.dismiss()
        }

        newNameDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        newNameDialog.show()
    }

    // Method to navigate to UserDashboardActivity
    private fun navigateToUserDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    // Method to navigate to SignInActivity
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    // Method to navigate to SignInActivity
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}
