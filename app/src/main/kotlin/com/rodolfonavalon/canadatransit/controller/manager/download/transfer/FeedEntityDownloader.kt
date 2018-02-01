package com.rodolfonavalon.canadatransit.controller.manager.download.transfer

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.download.DownloadManager
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.Maybe

/**
 * TransitFeedDownloader
 */
class TransitlandFeedEntityDownloader(downloadManager: DownloadManager, private val feedOneStopId: String):
        EntityDownloader<OperatorFeedVersion>(downloadManager) {

    override fun entityQuery(): Maybe<OperatorFeedVersion> {
        val transitDao = CanadaTransitApplication.appDatabase.transitLandDao()
        return transitDao.findOperatorFeedVersion(feedOneStopId)
    }
}

// TODO: more feed downloader