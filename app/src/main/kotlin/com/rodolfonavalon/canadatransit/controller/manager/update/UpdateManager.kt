package com.rodolfonavalon.canadatransit.controller.manager.update

import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask

class UpdateManager: AbstractQueueTask<UpdateTask>() {

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onComplete() {

    }

    companion object {
        private val instance: UpdateManager = UpdateManager()

    }
}