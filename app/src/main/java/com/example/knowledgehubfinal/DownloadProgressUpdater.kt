package com.example.knowledgehubfinal

import android.annotation.SuppressLint
import android.app.DownloadManager
import kotlinx.coroutines.delay

const val DOWNLOAD_SUCCESS = 100L
const val DOWNLOAD_FAILED = -100L

class DownloadProgressUpdater(
    private val manager: DownloadManager,
    private val downloadId: Long,
    private var listener: DownloadProgressListener
) {
    private val query = DownloadManager.Query()
    private var totalBytes = 0
    private val maxRetries = 100 // Set a max retry limit

    init {
        query.setFilterById(downloadId)
    }

    @SuppressLint("Range")
    suspend fun run() {
        var retries = 0
        while (downloadId > 0 && retries < maxRetries) {
            delay(250)
            retries++

            manager.query(query).use { cursor ->
                if (cursor.moveToFirst()) {
                    val totalSizeColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val bytesDownloadedColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

                    if (statusColumnIndex != -1 && bytesDownloadedColumnIndex != -1) {
                        if (totalSizeColumnIndex != -1 && totalBytes <= 0) {
                            totalBytes = cursor.getInt(totalSizeColumnIndex)
                        }

                        val downloadStatus = cursor.getInt(statusColumnIndex)
                        val bytesDownloadedSoFar = cursor.getInt(bytesDownloadedColumnIndex)

                        when (downloadStatus) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                listener.updateProgress(DOWNLOAD_SUCCESS)
                                return
                            }
                            DownloadManager.STATUS_FAILED -> {
                                listener.updateProgress(DOWNLOAD_FAILED)
                                return
                            }
                            else -> {
                                if (totalBytes > 0) {
                                    val downloadProgress = bytesDownloadedSoFar * 100L / totalBytes
                                    listener.updateProgress(downloadProgress)
                                } else {
                                    // If totalBytes is 0, report unknown progress
                                    listener.updateProgress(-1L)
                                }
                            }
                        }
                    }
                }
            }
        }

        // If we reach the maximum retry limit, assume the download failed
        if (retries >= maxRetries) {
            listener.updateProgress(DOWNLOAD_FAILED)
        }
    }

    interface DownloadProgressListener {
        fun updateProgress(progress: Long)
    }
}
