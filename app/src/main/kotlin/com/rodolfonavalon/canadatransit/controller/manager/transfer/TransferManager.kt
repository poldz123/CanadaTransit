package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.task.ObservableDownloaderTask
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueManager
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueManager

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor() : AbstractQueueManager<TransferTask>() {

    override fun onStartService() {
        // TODO add service to start the manager
    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadable: Downloadable) {
            instance.add(downloadable.trackingId(), ObservableDownloaderTask(instance, downloadable))
        }

        /**
         * Retrieves the instance of the [QueueManager] which is the instance of the [TransferManager], this
         * is to encapsulate the manager.
         */
        fun manager(): QueueManager<TransferTask> {
            return instance
        }
    }
}
