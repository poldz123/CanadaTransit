package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.extension.dbUpdate
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import io.reactivex.rxkotlin.addTo
import org.joda.time.DateTime
import timber.log.Timber

class UpdateOperatorFeedVersionTask : AbstractUpdateTask<List<OperatorFeedVersion>>() {
    private val userTransitDao = CanadaTransitApplication.appDatabase.userTransitDao()
    private val operatorFeedVersionDao = CanadaTransitApplication.appDatabase.operatorFeedVersionDao()

    private val userOperatorFeeds = mutableListOf<OperatorFeed>()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Querying user operator feeds...")
        userTransitDao.dbQuery { findOperatorFeeds() }
                .subscribe(::onFound, this::onError)
                .addTo(this.disposables)
    }

    private fun onFound(operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("No operator was found from selected operators.")
            onSaved(mutableListOf())
            return
        }
        Timber.d("Retrieving ${operatorFeeds.count()} operator feed versions...")
        userOperatorFeeds.addAll(operatorFeeds)
        TransitLandApi.retrieveOperatorFeedVersion(operatorFeeds,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(operatorFeedVersions: List<OperatorFeedVersion>) {
        if (operatorFeedVersions.isEmpty()) {
            Timber.d("No operator feeds version was found, this could mean that the API has a BUG.")
            onSaved(operatorFeedVersions)
            return
        }
        Timber.d("Saving ${operatorFeedVersions.count()} operator feed versions...")
        operatorFeedVersionDao.dbInsert {
            insert(operatorFeedVersions)
        }.subscribe({ _ ->
            // Once Operator feed version was successfully saved it should also
            // update the user operator's updated-at.
            val userTransits = mutableListOf<UserTransit>()
            for (userOperatorFeed in userOperatorFeeds) {
                userTransits.add(UserTransit(userOperatorFeed.operatorOneStopId, DateTime.now()))
            }
            userTransitDao.dbUpdate {
                update(userTransits)
            }.subscribe({ _ ->
                onSaved(operatorFeedVersions)
            }, this::onError)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(operatorFeedVersions: List<OperatorFeedVersion>) {
        Timber.d("Successfully saved ${operatorFeedVersions.count()} operator feed versions")
        this.onSuccess(operatorFeedVersions)
    }
}