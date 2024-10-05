package com.example.knowledgehubfinal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.knowledgehubfinal.databinding.EachPdfUserBinding
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.appcompat.app.AppCompatActivity


class PdfFileUserAdaptorActivity(
    pdfList: MutableList<PdfFile>,
    private val pdfClickListener: PdfClickListener
) : ListAdapter<PdfFile, PdfFileUserAdaptorActivity.PdfFileViewHolder>(PdfDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfFileViewHolder {
        // Inflate using the generated binding class for user layout
        val binding = EachPdfUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfFileViewHolder, position: Int) {
        val pdfFile = getItem(position)
        holder.bind(pdfFile)
    }

    inner class PdfFileViewHolder(private val binding: EachPdfUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pdfFile: PdfFile) {
            // Bind the PDF file name to the TextView in user layout
            binding.fileName.text = pdfFile.fileName

            // Handle delete click for user layout (this is actually the download icon in your layout)
            binding.imgPDF.setOnClickListener {
                downloadPdf(binding.root.context, pdfFile.fileName, pdfFile.downloadUrl)
            }

            // Implement renaming functionality for user layout
            binding.fileName.setOnClickListener {
                pdfClickListener.onPdfRename(adapterPosition, pdfFile.fileName)
            }

            // Hide EditText for renaming if needed in user layout
            binding.editFileName.visibility = View.GONE
        }

        private fun downloadPdf(context: Context, fileName: String, downloadUrl: String) {
            try {
                // Create a directory for downloads if it does not exist
                val downloadsDir = File(
                    ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOWNLOADS).firstOrNull(),
                    "KnowledgeHubDownloads"
                )
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                // File where the PDF will be downloaded
                val pdfFile = File(downloadsDir, fileName)
                if (pdfFile.exists()) {
                    Toast.makeText(context, "File already exists!", Toast.LENGTH_SHORT).show()
                    return
                }

                // Download the PDF file in a separate thread
                Thread {
                    try {
                        val url = URL(downloadUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connect()

                        val inputStream = connection.inputStream
                        val outputStream = FileOutputStream(pdfFile)

                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }

                        outputStream.close()
                        inputStream.close()

                        // Notify the user on the main thread after downloading
                        (context as? AppCompatActivity)?.runOnUiThread {
                            Toast.makeText(context, "Downloaded to: ${pdfFile.absolutePath}", Toast.LENGTH_SHORT).show()

                            // Optionally, open the downloaded file
                            openPdf(context, pdfFile)
                        }
                    } catch (e: Exception) {
                        Log.e("PdfFileUserAdaptor", "Error downloading PDF", e)
                        (context as? AppCompatActivity)?.runOnUiThread {
                            Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            } catch (e: Exception) {
                Log.e("PdfFileUserAdaptor", "Error downloading PDF", e)
                Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
            }
        }

        private fun openPdf(context: Context, pdfFile: File) {
            val pdfUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(pdfUri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
    }

    class PdfDiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean {
            return oldItem.downloadUrl == newItem.downloadUrl
        }

        override fun areContentsTheSame(oldItem: PdfFile, newItem: PdfFile): Boolean {
            return oldItem == newItem
        }
    }

    interface PdfClickListener {
        fun onPdfDeleted(position: Int)
        fun onPdfRename(position: Int, currentName: String)
        fun onPdfDownload(downloadUrl: String)
    }
}
