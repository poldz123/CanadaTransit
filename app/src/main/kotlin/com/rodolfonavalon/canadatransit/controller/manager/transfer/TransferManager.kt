package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.DownloadTransfer
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor() {
    private val queueKey = LinkedList<String>()
    private val queueMap = HashMap<String, Transfer>()

    private var active: Transfer? = null

    fun add(transfer: Transfer) {
        val trackingId = transfer.trackingId()
        if (queueKey.contains(trackingId) && queueMap.contains(trackingId)) {
            Timber.d("Transfer is already in progress with tracking-id: $trackingId")
            return
        }
        assert()
        // Add the tracking-id to the key and the transfer object to the map
        // and trigger the manager to start. No worries the transfer object
        // will only start when the manager has completed other queues.
        queueKey.add(trackingId)
        queueMap[trackingId] = transfer
        start()
    }

    fun remove(trackingId: String) {
        if (!queueKey.contains(trackingId) && !queueMap.contains(trackingId)) {
            Timber.d("Transfer does not exist with tracking-id: $trackingId")
            return
        }
        assert()
        // Whenever we remove a transfer object we also check that it is transferring so
        // the manager can trigger a cancel to the active transfer to properly dispose the
        // network call or even remove the temporary files.
        if (active?.trackingId() == trackingId) {
            active?.onCancel()
        } else {
            queueKey.remove(trackingId)
            queueMap.remove(trackingId)
        }
    }

    fun clear() {
        assert()
        // Clear the key and map of the transfer objects
        queueMap.clear()
        queueKey.clear()
        // Cancel the active transfer since remove all of the
        // transfer wont trigger cancel on it.
        active?.onCancel()
    }

    fun next() {
        // Empty queue key and map means that the manager is done
        if (queueKey.isEmpty() && queueMap.isEmpty()) {
            complete()
            return
        }
        assert()
        // Retrieve the key and value for the next transfer, this will
        // remove the key from the list of keys.
        val key = queueKey.pop()
        val value = queueMap[key]
        // Make sure to remove also the value from the map as well
        // whenever the key is removed from the list.
        DebugUtil.assertTrue(value != null, "Transfer was not found within map for key: $key!")
        queueMap.remove(key)
        // Initialize the active transfer and then trigger it to start.
        DebugUtil.assertTrue(active != null, "Active transfer was not de-initialized when starting a new transfer!")
        active = value
        active!!.onStart()
    }

    fun success(transfer: Transfer) {
        val trackingId = transfer.trackingId()
        Timber.d("Transferring data is a SUCCESS for tracking-id: $trackingId")
        assert()
        DebugUtil.assertTrue(queueKey.contains(trackingId) && queueMap.contains(trackingId), "The transfer object does not exist for tracking-id: $trackingId")
        // Make sure to de-initialize the active transfer object to enable the next transfer
        // object to be started.
        active = null
        remove(trackingId)
        next()
    }

    fun failure(transfer: Transfer) {
        val trackingId = transfer.trackingId()
        Timber.d("Transferring data is a FAILURE for tracking-id: $trackingId")
        assert()
        DebugUtil.assertTrue(queueKey.contains(trackingId) && queueMap.contains(trackingId), "The transfer object does not exist for tracking-id: $trackingId")
        // Make sure to de-initialize the active transfer object to enable the next transfer
        // object to be started.
        active = null
        remove(trackingId)
        next()
    }

    fun start() {
        if (active != null || (queueKey.isNotEmpty() && queueMap.isNotEmpty())) {
            Timber.d("Manager has already been started.")
            return
        }
        Timber.d("Preparing to start the transfer manager.")
        assert()
        next()
    }

    fun complete() {
        Timber.d("Manager has completed all of the transfers!")
    }

    fun assert() {
        DebugUtil.assertFalse(queueMap.isEmpty() != queueKey.isEmpty(), "Key and Map transfer has different size, this could mean that some transfers are not consumed!")
    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadTransfer: DownloadTransfer) {

        }
    }
}