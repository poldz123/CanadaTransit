package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

import io.reactivex.ObservableEmitter
import okio.Buffer
import okio.ForwardingSource
import okio.Source
import java.io.File

interface Downloader {
    fun onStart()
    fun onCancel()
    fun onError(error: Throwable)
}

/**
 * DownloadForwardingProperty
 */
data class DownloadForwardingProperty(val progress: Float? = null,
                                      val file: File? = null,
                                      val downloaded: Boolean = (file != null))

/**
 * DownloadForwardingSource
 */
class DownloadForwardingSource(source: Source,
                               private val totalBytesToRead: Long,
                               private val emitter: ObservableEmitter<DownloadForwardingProperty>): ForwardingSource(source) {
    var currentBytesRead: Long = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Read the next chunk of data
        val bytesRead = super.read(sink, byteCount)
        // Must check that the bytes being read is not yet complete
        if (isReadExhausted(bytesRead)) {
            currentBytesRead.plus(0L)
            emitter.onNext(DownloadForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead.plus(bytesRead)
            emitter.onNext(DownloadForwardingProperty(progress = bytesRead.toFloat() / totalBytesToRead.toFloat()))
        }
        return bytesRead
    }

    private fun isReadExhausted(bytesRead: Long): Boolean {
        return bytesRead == -1L
    }
}