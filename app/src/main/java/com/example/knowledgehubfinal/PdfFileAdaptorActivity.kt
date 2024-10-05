package com.example.knowledgehubfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.knowledgehubfinal.databinding.EachPdfItemBinding

class PdfFileAdaptorActivity(
    pdfList: MutableList<PdfFile>,
    private val pdfClickListener: PdfClickListener
) : ListAdapter<PdfFile, PdfFileAdaptorActivity.PdfFileViewHolder>(PdfDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfFileViewHolder {
        // Inflate using the generated binding class
        val binding = EachPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfFileViewHolder, position: Int) {
        val pdfFile = getItem(position)
        holder.bind(pdfFile)
    }

    inner class PdfFileViewHolder(private val binding: EachPdfItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pdfFile: PdfFile) {
            // Bind the PDF file name to the TextView
            binding.fileName.text = pdfFile.fileName

            // Handle delete click
            binding.imgDelete.setOnClickListener {
                pdfClickListener.onPdfDeleted(adapterPosition)
            }

            // If you want to implement renaming functionality
            binding.fileName.setOnClickListener {
                pdfClickListener.onPdfRename(adapterPosition, pdfFile.fileName)
            }

            // You can also show/hide the EditText for renaming if needed
            binding.editFileName.visibility = View.GONE
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
    }
}