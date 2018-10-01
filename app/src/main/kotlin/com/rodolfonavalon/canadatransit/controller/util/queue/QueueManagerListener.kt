package com.rodolfonavalon.canadatransit.controller.util.queue

interface QueueManagerListener {
	fun onSuccess(trackingId: String)
    fun onFailure(trackingId: String)
    fun onStart()
    fun onFinish()
}
