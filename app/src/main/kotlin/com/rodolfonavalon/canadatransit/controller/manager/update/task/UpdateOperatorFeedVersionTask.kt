package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
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
        Timber.d("Querying user operators feed version...")
        userOperatorDao.dbQuery { findOperatorFeeds() }
                .subscribe(::onUserOperatorFeedsFound, ::onError)
                .addTo(this.disposables)
    }

    private fun onUserOperatorFeedsFound(operatorFeed: List<OperatorFeed>) {
        if (operatorFeed.isEmpty()) {
            Timber.d("No operator was found from selected operators.")
            return
        }
        Timber.d("Retrieving ${operatorFeed.count()} operator feed versions...")
    }
}