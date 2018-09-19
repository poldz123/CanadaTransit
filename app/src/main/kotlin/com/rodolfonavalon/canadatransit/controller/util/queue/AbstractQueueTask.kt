package com.rodolfonavalon.canadatransit.controller.util.queue

import android.support.annotation.VisibleForTesting
import android.support.annotation.VisibleForTesting.PRIVATE
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.extension.safeLet
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber
import java.util.*

abstract class AbstractQueueTask<T: Task>: QueueTask<T> {
    private val queueKey = LinkedList<String>()
    private val queueTaskMap = HashMap<String, T>()
    private val queueObservableMap = HashMap<String, Observable<Any>>()

    private var activeTask: T? = null
    private var activeObservable: Observable<Any>? = null
    private var activeTrackingId: String? = null

    override var listener: QueueTaskListener? = null

    override fun add(trackingId: String, task: T): Observable<Any?> {
        if (queueKey.contains(trackingId)) {
            Timber.d("Transfer is already in progress with tracking-id: $trackingId")
            return queueObservableMap[trackingId]!!
        }
        assert()
        // Add the tracking-id to the key and the transfer object to the map
        // and trigger the manager to start. No worries the transfer object
        // will only start when the manager has completed other queues.
        queueKey.add(trackingId)
        queueTaskMap[trackingId] = task
        queueObservableMap[trackingId] = ReplaySubject.create(1)
        return queueObservableMap[trackingId]!!
    }

    override fun get(trackingId: String): T? {
        assert()
        if (isEmpty()) {
            return null
        }
        return queueTaskMap[trackingId]
    }

    override fun remove(trackingId: String): Boolean {
        if (!queueKey.contains(trackingId)) {
            Timber.d("Transfer does not exist with tracking-id: $trackingId")
            return false
        }
        assert()
        // Whenever we remove a transfer object we also check that it is transferring so
        // the manager can trigger a cancel to the active transfer to properly dispose the
        // network call or even remove the temporary files.
        activeTask?.let { task ->
            task.onCancel()
            // When a task is active it should call failure to execute the next task or finish the manager.
            failure()
        } ?: run {
            queueKey.remove(trackingId)
            queueTaskMap.remove(trackingId)
            queueObservableMap.remove(trackingId)
            // Make sure that onFailure is called
            listener?.onFailure(trackingId)
        }
        return true
    }

    override fun clear() {
        assert()

        // Clearing the task should only remove the task that are not yet running.
        //  - First stage:
        //      Remove all of the idle tasks except the active task .
        //  - Second stage:
        //      Call remove for the active task

        // First Stage
        val iterator = queueKey.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            // Active task should not be removed
            if (isBusy() && activeTrackingId == key) {
                continue
            }
            // Remove the key and task in the queue
            iterator.remove()
            queueTaskMap.remove(key)
            queueObservableMap.remove(key)
            // Make sure that onFailure is called
            listener?.onFailure(key)
        }

        // Second Stage
        activeTrackingId?.let { trackingId ->
            remove(trackingId)
        }
    }

    override fun next() {
        // Make sure to reset the data of the active transfer and remove
        // it from the queue for the next transfer
        activeTrackingId?.let { trackingId ->
            val removedTrackingId = queueKey.poll()
            val removedTransfer = queueTaskMap.remove(trackingId)
            val removedObservable = queueObservableMap.remove(trackingId)
            DebugUtil.assertEqual(removedTrackingId, trackingId, "Tracking ID did not match. EXPECTED: $removedTrackingId CURRENT: $activeTrackingId")
            DebugUtil.assertEqual(removedTransfer, activeTask, "Transfer Task did not match.")
            DebugUtil.assertEqual(removedObservable, activeObservable, "Transfer Observable did not match.")
        }
        activeTask = null
        activeTrackingId = null
        activeObservable = null
        // Empty queue key and map means that the manager is done
        if (isEmpty()) {
            listener?.onFinish()
            return
        }
        assert()
        // Retrieve the key and value for the next transfer, this will
        // not remove the key from the list of keys.
        safeLet(queueKey.peek(), queueTaskMap[queueKey.peek()], queueObservableMap[queueKey.peek()]) { key, task, observable ->
            // Initialize the active transfer
            activeTrackingId = key
            activeTask = task
            activeObservable = observable
            // Trigger the task to start
            task.onStart(key, observable)
        }
    }

    override fun start() {
        if (isBusy()) {
            Timber.d("Manager has already been started.")
            return
        }
        Timber.d("Preparing to start the transfer manager.")
        listener?.onStart()
        assert()
        next()
    }

    fun success() {
        DebugUtil.assertNotNull(activeTrackingId, "Active tracking id is NULL.")
        Timber.d("Transferring data is a SUCCESS for tracking-id: $activeTrackingId")
        activeTrackingId?.let { trackingId ->
            assert()
            listener?.onSuccess(trackingId)
            next()
        }
    }

    fun failure() {
        DebugUtil.assertNotNull(activeTrackingId, "Active tracking id is NULL.")
        Timber.d("Transferring data is a FAILURE for tracking-id: $activeTrackingId")
        activeTrackingId?.let { trackingId ->
            assert()
            listener?.onFailure(trackingId)
            next()
        }
    }

    // TODO Remove??
    private fun assert() {
        DebugUtil.assertEqual(queueTaskMap.isEmpty(), queueKey.isEmpty(), "Key and Map transfer has different size, this could mean that some transfers are not consumed!")
    }

    fun isEmpty(): Boolean = queueKey.isEmpty() && queueTaskMap.isEmpty() && queueObservableMap.isEmpty()

    fun isBusy(): Boolean = activeTask != null && activeTrackingId != null && activeObservable != null

    @VisibleForTesting(otherwise = PRIVATE)
    fun numTasks(): Int = queueKey.count()
}
