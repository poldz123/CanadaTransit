package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.task.ObservableDownloaderTask
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor() : AbstractQueueTask<TransferTask>() {

    override fun onStartService() {
        // TODO add service to start the manager
    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadable: Downloadable) {
            instance.add(downloadable.trackingId(), ObservableDownloaderTask(instance, downloadable))
        }
    }
}
