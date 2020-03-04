package com.rodolfonavalon.canadatransit.controller.util.queue.task

typealias OnSuccessTaskListener<T> = (T) -> Unit
typealias OnFailureTaskListener = () -> Unit

interface TaskListener {
    fun onSuccess(trackingId: String)
    fun onFailure(trackingId: String)
    fun onStart()
    fun onFinish()
}
