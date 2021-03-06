package com.rodolfonavalon.canadatransit.controller.manager.transfer.util

import io.reactivex.ObservableEmitter
import java.io.File
import okio.Buffer
import okio.ForwardingSource
import okio.Source

/**
 * TODO: TransferForwardingSource
 */
class TransferForwardingSource(
    source: Source,
    private val totalBytesToRead: Long,
    private val emitter: ObservableEmitter<TransferForwardingProperty>
) : ForwardingSource(source) {
    private var currentBytesRead: Long = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Read the next chunk of data
        val bytesRead = super.read(sink, byteCount)
        // Must check that the bytes being read is not yet complete
        if (isReadExhausted(bytesRead)) {
            currentBytesRead += 0L
            emitter.onNext(TransferForwardingProperty(progress = 1.0F))
        } else {
            currentBytesRead += bytesRead
            emitter.onNext(TransferForwardingProperty(progress = currentBytesRead / totalBytesToRead.toFloat()))
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

data class TransferForwardingProperty(
    val progress: Float = 0f,
    val filePath: String = "",
    val transferred: Boolean = filePath != "",
    val transferredFile: File = File(filePath)
)
