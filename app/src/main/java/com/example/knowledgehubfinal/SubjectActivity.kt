package com.example.knowledgehubfinal

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SubjectActivity : AppCompatActivity(), PdfFileUserAdaptorActivity.PdfClickListener {

    private lateinit var pdfRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFileUserAdaptorActivity
    private val pdfList = mutableListOf<PdfFile>()
    private var downloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        // Initialize RecyclerView for PDFs
        pdfRecyclerView = findViewById(R.id.UserPdfRecylerview)
        pdfRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize PdfFileUserAdaptorActivity with pdfList and click listener
        adapter = PdfFileUserAdaptorActivity(pdfList, this)
        pdfRecyclerView.adapter = adapter

        // Fetch PDF list from Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("pdfs")
        fetchPdfFiles()

        // Set up button navigation
        setupButtonNavigation()
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
                Toast.makeText(this@SubjectActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPdfDeleted(position: Int) {
        // Handle PDF deletion (if needed)
    }

    override fun onPdfRename(position: Int, currentName: String) {
        // Handle PDF renaming (if needed)
    }

    override fun onPdfDownload(downloadUrl: String) {
        this.downloadUrl = downloadUrl
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
            } else {
                downloadPdf(downloadUrl)
            }
        } else {
            downloadPdf(downloadUrl)
        }
    }

    // Method to download PDF
    private fun downloadPdf(pdfUrl: String) {
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
        request.setTitle("Downloading PDF...")
        request.setDescription("Downloading ${pdfUrl.substringAfterLast('/')}")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "KnowledgeHubDownloads/${pdfUrl.substringAfterLast('/')}")
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    // Handle the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadUrl?.let { downloadPdf(it) }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonNavigation() {
        // Navigation to other activities
        findViewById<ImageView>(R.id.navNotifications).setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }
        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        // Navigate to DashboardActivity when UserHome is clicked
        findViewById<ImageView>(R.id.UserHome).setOnClickListener {
            startActivity(Intent(this, UserDashboardActivity::class.java))
        }

        // Navigate to SignInActivity when logout button is clicked
        findViewById<ImageView>(R.id.logout).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }


    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }
}
