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
        Timber.d("Querying user operators...")
        userTransitDao.dbQuery { findOperators() }
                .subscribe(::onFound, this::onError)
                .addTo(this.disposables)
    }

    private fun onFound(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("No operators was selected by the user.")
            onSaved(mutableListOf())
            return
        }
        Timber.d("Retrieving ${operators.count()} operator feeds...")
        TransitLandApi.retrieveOperatorFeed(operators,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("No operator feeds was found, this could mean that the API has a BUG.")
            onSaved(operatorFeeds)
            return
        }
        Timber.d("Saving ${operatorFeeds.count()} operator feeds...")
        operatorFeedDao.dbInsert {
            insert(operatorFeeds)
        }.subscribe({ _ ->
            onSaved(operatorFeeds)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(operatorFeeds: List<OperatorFeed>) {
        Timber.d("Successfully saved ${operatorFeeds.count()} operator feeds")
        this.onSuccess(operatorFeeds)
    }
}
