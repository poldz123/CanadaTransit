package com.rodolfonavalon.canadatransit.controller.manager.transfer.download

import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transfer
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.ResponseBody
import okio.Buffer
import okio.ForwardingSource
import okio.Source
import retrofit2.Response
import java.io.File

/**
 * TODO: DownloadTransfer
 */
interface DownloadTransfer: Transfer {

    /**
     * TODO: onProgress
     */
    fun onProgress(property: DownloadForwardingProperty)

    /**
     * TODO: willDownload
     */
    fun willDownload(responseBody: Response<ResponseBody>): Observable<DownloadForwardingProperty>

    /**
     * TODO: didDownload
     */
    fun didDownload(file:File?)
}

/**
 * TODO: DownloadForwardingProperty
 */
data class DownloadForwardingProperty(val progress: Float? = null,
                                      val file: File? = null,
                                      val downloaded: Boolean = (file != null))

/**
 * TODO: DownloadForwardingSource
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
            currentBytesRead += 0L
            emitter.onNext(DownloadForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead += bytesRead
            emitter.onNext(DownloadForwardingProperty(progress = bytesRead.toFloat() / totalBytesToRead.toFloat()))
        }
        return bytesRead
    }

    private fun isReadExhausted(bytesRead: Long): Boolean {
        return bytesRead == -1L
    }
}