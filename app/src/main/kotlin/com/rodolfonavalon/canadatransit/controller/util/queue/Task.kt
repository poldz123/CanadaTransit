package com.rodolfonavalon.canadatransit.controller.util.queue

interface Task {

    /**
     * TODO: onStart
     */
    fun onStart()

    /**
     * TODO: onCancel
     */
    fun onCancel()

    /**
     * TODO: onError
     */
    fun onError(error: Throwable)
}