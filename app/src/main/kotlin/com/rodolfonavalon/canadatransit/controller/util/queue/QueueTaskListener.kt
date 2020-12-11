package com.rodolfonavalon.canadatransit.controller.util.queue

interface QueueTaskListener {
	fun onSuccess(trackingId: String)
    fun onFailure(trackingId: String)
    fun onStart()
    fun onFinish()
}
