package com.rodolfonavalon.canadatransit.controller.manager.transfer

import com.rodolfonavalon.canadatransit.controller.manager.transfer.task.ObservableDownloaderTask
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask

/**
 * TODO: DownloadManager
 */
class TransferManager private constructor(): AbstractQueueTask<TransferTask>() {

    override fun complete() {

    }

    companion object {
        private val instance: TransferManager = TransferManager()

        fun download(downloadable: Transferable.Downloadable) {
            instance.add(downloadable.trackingId(), ObservableDownloaderTask(instance, downloadable))
        }
    }
}


