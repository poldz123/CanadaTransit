package com.rodolfonavalon.canadatransit.controller.util.queue

import io.reactivex.Observer

interface Task {

    /**
     * TODO: onStart
     */
    fun onStart(trackingId: String, callbackObserver: Observer<Any>)

    /**
     * TODO: onCancel
     */
    fun onCancel()

    /**
     * TODO: onError
     */
    fun onError(error: Throwable)
}
