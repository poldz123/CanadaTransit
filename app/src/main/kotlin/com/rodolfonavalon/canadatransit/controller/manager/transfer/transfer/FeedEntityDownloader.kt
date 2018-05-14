package com.rodolfonavalon.canadatransit.controller.manager.transfer.transfer

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.Maybe

/**
 * TransitFeedDownloader
 */
class TransitlandFeedEntityDownloader(downloadManager: TransferManager, private val feedOneStopId: String):
        EntityDownloader<OperatorFeedVersion>(downloadManager) {

    override fun entityQuery(): Maybe<OperatorFeedVersion> {
        val transitDao = CanadaTransitApplication.appDatabase.transitLandDao()
        return transitDao.findOperatorFeedVersion(feedOneStopId)
    }
}

// TODO: more feed downloader