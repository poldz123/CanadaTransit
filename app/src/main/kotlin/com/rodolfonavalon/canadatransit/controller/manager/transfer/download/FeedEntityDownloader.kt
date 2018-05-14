package com.rodolfonavalon.canadatransit.controller.manager.transfer.download

import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion

/**
 * TransitFeedDownloader
 */
class TransitlandFeedEntityDownloader(downloadManager: TransferManager, private val operatorFeedVersion: OperatorFeedVersion):
        EntityDownloader<OperatorFeedVersion>(downloadManager) {

    override fun entity(): OperatorFeedVersion {
       return operatorFeedVersion
    }
}

// TODO: more feed downloader