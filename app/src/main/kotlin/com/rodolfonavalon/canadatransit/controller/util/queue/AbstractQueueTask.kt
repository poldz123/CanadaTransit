package com.rodolfonavalon.canadatransit.controller.util.queue

import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import timber.log.Timber
import java.util.*

abstract class AbstractQueueTask<T: Task>: QueueTask<T> {
    private val queueKey = LinkedList<String>()
    private val queueMap = HashMap<String, T>()

    private var activeTask: T? = null
    private var activeTrackingId: String? = null

    abstract fun onSuccess(trackingId: String)
    abstract fun onFailure(trackingId: String)
    abstract fun onComplete()

    override fun add(trackingId: String, task: T) {
        if (queueKey.contains(trackingId) && queueMap.contains(trackingId)) {
            Timber.d("Transfer is already in progress with tracking-id: $trackingId")
            return
        }
        assert()
        // Add the tracking-id to the key and the transfer object to the map
        // and trigger the manager to start. No worries the transfer object
        // will only start when the manager has completed other queues.
        queueKey.add(trackingId)
        queueMap[trackingId] = task
    }

    override fun get(trackingId: String): T? {
        assert()
        if (isEmpty()) {
            return null
        }
        if (queueMap[trackingId] != null) {
            return queueMap[trackingId]
        }
        if (isBusy() && activeTrackingId == trackingId) {
            return activeTask
        }
        return null
    }

    override fun remove(trackingId: String) {
        if (!queueKey.contains(trackingId) && !queueMap.contains(trackingId)) {
            Timber.d("Transfer does not exist with tracking-id: $trackingId")
            return
        }
        assert()
        // Whenever we remove a transfer object we also check that it is transferring so
        // the manager can trigger a cancel to the active transfer to properly dispose the
        // network call or even remove the temporary files.
        if (activeTrackingId == trackingId) {
            DebugUtil.assertTrue(activeTask != null, "Active transfer is null, did you initialized it!?")
            activeTask!!.onCancel()
        } else {
            queueKey.remove(trackingId)
            queueMap.remove(trackingId)
        }
    }

    override fun clear() {
        assert()
        // Clear the key and map of the transfer objects
        queueMap.clear()
        queueKey.clear()
        // Cancel the active transfer since remove all of the
        // transfer wont trigger cancel on it.
        activeTask?.onCancel()
    }

    override fun next() {
        // Make sure to reset the data of the active transfer and remove
        // it from the queue for the next transfer
        if (activeTrackingId != null && activeTask != null) {
            val removedTrackingId = queueKey.poll()
            val removedTransfer = queueMap.remove(activeTrackingId!!)
            DebugUtil.assertTrue(removedTrackingId == activeTrackingId, "Tracking ID did not match. EXPECTED: $removedTrackingId CURRENT: $activeTrackingId")
            DebugUtil.assertTrue(removedTransfer == activeTask, "Transfer OBJECT did not match.")
        }
        activeTask = null
        activeTrackingId = null
        // Empty queue key and map means that the manager is done
        if (isEmpty()) {
            onComplete()
            return
        }
        assert()
        // Retrieve the key and value for the next transfer, this will
        // not remove the key from the list of keys.
        val key = queueKey.peek()
        val value = queueMap[key]
        // Make sure to debug the key and value that it exist
        DebugUtil.assertTrue(value != null, "Transfer was not found within map for key: $key!")
        // Initialize the active transfer and then trigger it to start.
        activeTrackingId = key
        activeTask = value
        activeTask!!.onStart(activeTrackingId!!)
    }

    override fun start() {
        if (isBusy()) {
            Timber.d("Manager has already been started.")
            return
        }
        Timber.d("Preparing to start the transfer manager.")
        assert()
        next()
    }

    fun success() {
        DebugUtil.assertTrue(activeTrackingId != null, "Active tracking id is NULL.")
        Timber.d("Transferring data is a SUCCESS for tracking-id: $activeTrackingId")
        assert()
        onSuccess(activeTrackingId!!)
        next()
    }

    fun failure() {
        DebugUtil.assertTrue(activeTrackingId != null, "Active tracking id is NULL.")
        Timber.d("Transferring data is a FAILURE for tracking-id: $activeTrackingId")
        assert()
        onFailure(activeTrackingId!!)
        next()
    }

    private fun assert() {
        DebugUtil.assertTrue(queueMap.isEmpty() == queueKey.isEmpty(), "Key and Map transfer has different size, this could mean that some transfers are not consumed!")
    }

    fun isEmpty(): Boolean = queueKey.isEmpty() && queueMap.isEmpty() && !isBusy()

    fun isBusy(): Boolean = (activeTask != null && activeTrackingId != null)
}