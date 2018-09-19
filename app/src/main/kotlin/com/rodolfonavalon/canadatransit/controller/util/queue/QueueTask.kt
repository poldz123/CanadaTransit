package com.rodolfonavalon.canadatransit.controller.util.queue

import io.reactivex.Observable

interface QueueTask<T: Task> {
	// Listener for the queue
    var listener: QueueTaskListener?

    fun add(trackingId: String, task: T): Observable<Any?>
    fun remove(trackingId: String): Boolean
    fun get(trackingId: String): T?
    fun next()
    fun clear()
    fun start()
}
