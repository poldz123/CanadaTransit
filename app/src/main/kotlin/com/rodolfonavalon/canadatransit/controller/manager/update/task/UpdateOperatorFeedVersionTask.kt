package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class UpdateOperatorFeedVersionTask(private val updateManager: UpdateManager) :  AbstractObservableTask<List<OperatorFeedVersion>>() {
    private val userOperatorDao = CanadaTransitApplication.appDatabase.userOperatorsDao()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Querying user operator feeds...")
        userOperatorDao.dbQuery { findOperatorFeeds() }
                .subscribe(::onUserOperatorFeedsFound, ::onError)
                .addTo(this.disposables)
    }

    private fun onUserOperatorFeedsFound(operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("No operator was found from selected operators.")
            return
        }
        Timber.d("Retrieving ${operatorFeeds.count()} operator feed versions...")
        TransitLandApi.retrieveOperatorFeedVersion(operatorFeeds,
                ::onOperatorFeedVersionsReceived,
                ::onError)
                .addTo(this.disposables)
    }

    private fun onOperatorFeedVersionsReceived(operatorFeedVersions: List<OperatorFeedVersion>) {

    }
}