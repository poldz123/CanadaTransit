package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class UpdateOperatorFeedTask : AbstractUpdateTask<List<OperatorFeed>>() {
    private val operatorFeedDao = CanadaTransitApplication.appDatabase.operatorFeedDao()
    private val userTransitDao = CanadaTransitApplication.appDatabase.userTransitDao()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("FEED: Querying user operators...")
        userTransitDao.dbQuery { findOperators() }
                .subscribe(::onFound, this::onError)
                .addTo(this.disposables)
    }

    private fun onFound(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("FEED: No operators was selected by the user.")
            onSaved(mutableListOf())
            return
        }
        Timber.d("FEED: Fetching ${operators.count()} operator feeds...")
        TransitLandApi.retrieveOperatorFeed(operators,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(operatorFeeds: List<OperatorFeed>) {
        // Need to filter out empty current feed version since they do not have
        // any feed version for the operator
        Timber.d("FEED: Filtering ${operatorFeeds.count()} operator feeds...")
        val filteredOperatorFeeds = operatorFeeds.filter { it.currentFeedVersion.isNotEmpty() }
        Timber.d("FEED: Saving ${filteredOperatorFeeds.count()} operator feeds...")
        operatorFeedDao.dbInsert {
            nuke()
            insert(filteredOperatorFeeds)
        }.subscribe({ _ ->
            onSaved(filteredOperatorFeeds)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(operatorFeeds: List<OperatorFeed>) {
        Timber.d("FEED: Successfully saved ${operatorFeeds.count()} operator feeds")
        this.onSuccess(operatorFeeds)
    }
}
