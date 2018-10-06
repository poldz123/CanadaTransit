package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.extension.dbUpdate
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.rxkotlin.addTo
import org.joda.time.DateTime
import timber.log.Timber

class UpdateOperatorFeedVersionTask : AbstractUpdateTask<List<OperatorFeedVersion>>() {
    private val userTransitDao = CanadaTransitApplication.appDatabase.userTransitDao()
    private val operatorFeedVersionDao = CanadaTransitApplication.appDatabase.operatorFeedVersionDao()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("VERSION: Querying user operator feeds...")
        userTransitDao.dbQuery { findFeeds() }
                .subscribe(::onFound, this::onError)
                .addTo(this.disposables)
    }

    private fun onFound(feeds: List<Feed>) {
        if (feeds.isEmpty()) {
            Timber.d("VERSION: No operator was found from selected operators.")
            onSaved(mutableListOf())
            return
        }
        Timber.d("VERSION: Fetching ${feeds.count()} operator feed versions...")
        TransitLandApi.retrieveOperatorFeedVersion(feeds,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(operatorFeedVersions: List<OperatorFeedVersion>) {
        Timber.d("VERSION: Saving ${operatorFeedVersions.count()} operator feed versions...")
        operatorFeedVersionDao.dbInsert {
            insert(operatorFeedVersions)
        }.subscribe({ _ ->
            // Once Operator feed version was successfully saved it should also
            // update all of the user operator's updated-at.
            userTransitDao.dbUpdate {
                updateAll(DateTime.now())
            }.subscribe({ _ ->
                onSaved(operatorFeedVersions)
            }, this::onError)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(operatorFeedVersions: List<OperatorFeedVersion>) {
        Timber.d("VERSION: Successfully saved ${operatorFeedVersions.count()} operator feed versions")
        this.onSuccess(operatorFeedVersions)
    }
}