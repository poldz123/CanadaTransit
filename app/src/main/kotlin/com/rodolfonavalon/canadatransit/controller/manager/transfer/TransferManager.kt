package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.download.ObservableDownloader
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil.assertTrue
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor() {
    private val queueKey = LinkedList<String>()
    private val queueMap = HashMap<String, Transfer>()

    private var activeTransfer: Transfer? = null
    private var activeTrackingId: String? = null

    fun add(trackingId: String, transfer: Transfer) {
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
        if (activeTrackingId == trackingId) {
            assertTrue(activeTransfer != null, "Active transfer is null, did you initialized it!?")
            activeTransfer!!.onCancel()
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
        activeTransfer?.onCancel()
    }

    fun next() {
        // Make sure to reset the data of the active transfer and remove
        // it from the queue for the next transfer
        if (activeTrackingId != null && activeTransfer != null) {
            val removedTrackingId = queueKey.poll()
            val removedTransfer = queueMap.remove(activeTrackingId!!)
            assertTrue(removedTrackingId == activeTrackingId, "Tracking ID did not match. EXPECTED: $removedTrackingId CURRENT: $activeTrackingId")
            assertTrue(removedTransfer == activeTransfer, "Transfer OBJECT did not match.")
        }
        activeTransfer = null
        activeTrackingId = null
        // Empty queue key and map means that the manager is done
        if (isEmpty()) {
            complete()
            return
        }
        assert()
        // Retrieve the key and value for the next transfer, this will
        // not remove the key from the list of keys.
        val key = queueKey.peek()
        val value = queueMap[key]
        // Make sure to debug the key and value that it exist
        assertTrue(value != null, "Transfer was not found within map for key: $key!")
        // Initialize the active transfer and then trigger it to start.
        activeTrackingId = key
        activeTransfer = value
        activeTransfer!!.onStart()
    }

    fun success(trackingId: String) {
        assertTrue(trackingId == activeTrackingId, "Tracking ID did not match. EXPECTED: $trackingId CURRENT: $activeTrackingId")
        Timber.d("Transferring data is a SUCCESS for tracking-id: $activeTrackingId")
        assert()
        next()
    }

    fun failure(trackingId: String) {
        assertTrue(trackingId == activeTrackingId, "Tracking ID did not match. EXPECTED: $trackingId CURRENT: $activeTrackingId")
        Timber.d("Transferring data is a FAILURE for tracking-id: $activeTrackingId")
        assert()
        next()
    }

    fun start() {
        if (isBusy()) {
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
        assertTrue(queueMap.isEmpty() == queueKey.isEmpty(), "Key and Map transfer has different size, this could mean that some transfers are not consumed!")
    }

    fun isEmpty(): Boolean = queueKey.isEmpty() && queueMap.isEmpty()

    fun isBusy(): Boolean = (activeTransfer != null && activeTrackingId != null)

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadable: Transferable.Downloadable) {
            instance.add(downloadable.transferTrackingId(), ObservableDownloader(instance, downloadable))
        }
    }
}