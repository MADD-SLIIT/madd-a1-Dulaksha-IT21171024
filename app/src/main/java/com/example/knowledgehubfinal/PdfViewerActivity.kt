package com.example.knowledgehubfinal

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.knowledgehubfinal.databinding.ActivityPdfViewerBinding
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.URL

class PdfViewerActivity : AppCompatActivity(), DownloadProgressUpdater.DownloadProgressListener {

    private lateinit var binding: ActivityPdfViewerBinding
    private lateinit var downloadManager: DownloadManager
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get passed extras from intent
        val fileName = intent.extras?.getString("fileName")
        val downloadUrl = intent.extras?.getString("downloadUrl")

        // Log to check the download URL
        Log.d("PdfViewerActivity", "Download URL: $downloadUrl")

        // Initialize download manager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        snackbar = Snackbar.make(binding.mainlayout, "", Snackbar.LENGTH_INDEFINITE)

        // Check permissions before proceeding
        checkPermissions()

        // Start downloading and viewing the PDF
        lifecycleScope.launch(Dispatchers.IO) {
            val savedFile = savePdfToLocal(downloadUrl, fileName)

            savedFile?.let {
                withContext(Dispatchers.Main) {
                    binding.pdfView.fromFile(it)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .onLoad { pages ->
                            binding.progressBar.visibility = View.GONE
                        }
                        .onError { t ->
                            Toast.makeText(
                                this@PdfViewerActivity,
                                "Error loading PDF: ${t.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .load()
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PdfViewerActivity,
                        "Failed to download PDF.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Download button click listener
        binding.floatingActionButton.setOnClickListener {
            downloadPdf(downloadUrl, fileName)
        }
    }

    // Check permissions at runtime
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    // Save PDF file locally
    private fun savePdfToLocal(downloadUrl: String?, fileName: String?): File? {
        return try {
            val url = URL(downloadUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream: InputStream = connection.getInputStream()
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

            file.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Download PDF file using DownloadManager
    private fun downloadPdf(downloadUrl: String?, fileName: String?) {
        try {
            val downloadUri = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + fileName
                )

            val downloadId = downloadManager.enqueue(request)
            binding.progressBar.visibility = View.VISIBLE
            val downloadProgressHelper = DownloadProgressUpdater(downloadManager, downloadId, this)

            // Start tracking download progress
            lifecycleScope.launch(Dispatchers.IO) {
                downloadProgressHelper.run()
            }
            snackbar.show()

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Update download progress
    override fun updateProgress(progress: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (progress) {
                DOWNLOAD_SUCCESS -> {
                    snackbar.setText("Download completed.")
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@PdfViewerActivity,
                        "Downloaded Successfully!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    snackbar.dismiss()
                }
                DOWNLOAD_FAILED -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@PdfViewerActivity,
                        "Download Failed!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    snackbar.dismiss()
                }
                else -> {
                    binding.progressBar.progress = progress.toInt()
                    snackbar.setText("Downloading... $progress%")
                }
            }
        }
    }
}
