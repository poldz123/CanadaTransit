package com.rodolfonavalon.canadatransit.controller.manager.transfer.util

import io.reactivex.ObservableEmitter
import okio.Buffer
import okio.ForwardingSource
import okio.Source
import java.io.File

/**
 * TODO: TransferForwardingSource
 */
class TransferForwardingSource(source: Source,
                               private val totalBytesToRead: Long,
                               private val emitter: ObservableEmitter<TransferForwardingProperty>): ForwardingSource(source) {
    var currentBytesRead: Long = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Read the next chunk of data
        val bytesRead = super.read(sink, byteCount)
        // Must check that the bytes being read is not yet complete
        if (isReadExhausted(bytesRead)) {
            currentBytesRead += 0L
            emitter.onNext(TransferForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead += bytesRead
            emitter.onNext(TransferForwardingProperty(progress = bytesRead.toFloat() / totalBytesToRead.toFloat()))
        }
        return bytesRead
    }

    private fun isReadExhausted(bytesRead: Long): Boolean {
        return bytesRead == -1L
    }
}

/**
 * TODO: TransferForwardingProperty
 */
data class TransferForwardingProperty(val progress: Float? = null,
                                      val file: File? = null,
                                      val transferred: Boolean = (file != null))
