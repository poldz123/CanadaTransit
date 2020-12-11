package com.rodolfonavalon.canadatransit.controller.util.queue.task

interface Task {

    /**
     * TODO: onStart
     */
    fun onStart(trackingId: String)

    /**
     * TODO: onCancel
     */
    fun onCancel()

    /**
     * TODO: onError
     */
    fun onError(error: Throwable)
}
