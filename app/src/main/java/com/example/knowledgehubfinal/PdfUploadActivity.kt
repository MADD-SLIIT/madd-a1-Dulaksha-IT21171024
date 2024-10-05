package com.example.knowledgehubfinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.knowledgehubfinal.databinding.ActivityPdfUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PdfUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfUploadBinding
    private var pdfFileUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        initClickListeners()
        initBottomNavigationClickListeners()

    }

    private fun init() {
        binding = ActivityPdfUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().reference.child("pdfs")
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")

    }

    private fun initClickListeners() {
        binding.selectPdfButton.setOnClickListener {
            launcher.launch("application/pdf")
        }

        binding.uploadBtn.setOnClickListener {
            if (pdfFileUri != null) {
                uploadPdfFileToFirebase()
            } else {
                Toast.makeText(this, "Please select pdf first", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to MathsPdfActivity when showAllBtn is clicked
        binding.showAllBtn.setOnClickListener {
            val intent = Intent(this, MathsPdfActivity::class.java)
            startActivity(intent)
        }
    }

        // Initialize bottom navigation button listeners
        private fun initBottomNavigationClickListeners() {
            // Navigate to DashboardActivity when navHome is clicked
            binding.navHome.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }

            // Navigate to SignInActivity when logout is clicked
            binding.logout.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            // Navigate to ProfileActivity when navProfile is clicked
            binding.navProfile.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        private val launcher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                pdfFileUri = uri
                val fileName = uri?.let { DocumentFile.fromSingleUri(this, it)?.name }
                binding.fileName.text = fileName.toString()
            }

        private fun uploadPdfFileToFirebase() {
            val fileName = binding.fileName.text.toString()
            val mStorageRef = storageReference.child("${System.currentTimeMillis()}/$fileName")

            pdfFileUri?.let { uri ->
                mStorageRef.putFile(uri).addOnSuccessListener {
                    mStorageRef.downloadUrl.addOnSuccessListener { downloadUri ->

                        val pdfFile = PdfFile(fileName, downloadUri.toString())
                        databaseReference.push().key?.let { pushKey ->
                            databaseReference.child(pushKey).setValue(pdfFile)
                                .addOnSuccessListener {

                                    pdfFileUri = null
                                    binding.fileName.text =
                                        resources.getString(R.string.no_pdf_file_selected_yet)
                                    Toast.makeText(
                                        this,
                                        "Uploaded Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (binding.progressBar.isShown)
                                        binding.progressBar.visibility = View.GONE

                                    // Immediately go back to the list screen without restarting the activity
                                    finish()  // Close this activity
                                }.addOnFailureListener { err ->
                                    Toast.makeText(this, err.message.toString(), Toast.LENGTH_SHORT)
                                        .show()

                                    if (binding.progressBar.isShown)
                                        binding.progressBar.visibility = View.GONE
                                }
                        }
                    }
                }.addOnProgressListener { uploadTask ->
                    val uploadingPercent =
                        uploadTask.bytesTransferred * 100 / uploadTask.totalByteCount
                    binding.progressBar.progress = uploadingPercent.toInt()
                    if (!binding.progressBar.isShown)
                        binding.progressBar.visibility = View.VISIBLE

                }.addOnFailureListener { err ->
                    if (binding.progressBar.isShown)
                        binding.progressBar.visibility = View.GONE

                    Toast.makeText(this, err.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

