package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.task.ObservableDownloaderTask
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueTask

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor(): AbstractQueueTask<TransferTask>() {

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onStart() {

    }

    override fun onFinish() {

    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadable: Downloadable) {
            instance.add(downloadable.trackingId(), ObservableDownloaderTask(instance, downloadable))
        }
    }
}


