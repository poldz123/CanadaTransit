package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.DownloadTransfer
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import java.util.*

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor() {
    private val queue = LinkedList<Transfer>()
    private var active: Transfer? = null

    var isBusy: Boolean = false
        private set

    fun add() {

    }

    fun remove() {

    }

    fun cancel(all: Boolean = false) {

    }

    fun next() {
        // Empty queue means that download manager is done
        if (queue.isEmpty()) {
            done()
            return
        }

        DebugUtil.assertTrue(active != null, "Active downloader was not de-initialized!")
        active = queue.pop()
        active!!.onStart()
    }

    fun done() {

    }

    fun success() {
        next()
    }

    fun failure() {
        next()
    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadTransfer: DownloadTransfer) {

        }

        fun generateTrackingKey(): String {
            // TODO: generate tracking key
            return ""
        }
    }
}