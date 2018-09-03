package com.rodolfonavalon.canadatransit.controller.util.queue

interface QueueTask<T: Task> {
    fun add(trackingId: String, task: T)
    fun remove(trackingId: String)
    fun get(trackingId: String): T?
    fun next()
    fun clear()
    fun start()
}