package com.rodolfonavalon.canadatransit.controller.util.queue

interface QueueTask<T> {
	// Listener for the queue
    var listener: QueueTaskListener?

    fun <R : T> add(trackingId: String, task: R): R
    fun remove(trackingId: String): Boolean
    fun get(trackingId: String): T?
    fun next()
    fun clear()
    fun start()
}
