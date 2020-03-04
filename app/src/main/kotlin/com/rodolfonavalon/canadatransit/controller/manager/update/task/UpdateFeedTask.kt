package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class UpdateFeedTask : AbstractUpdateTask<List<Feed>>() {
    private val feedDao = CanadaTransitApplication.appDatabase.feedDao()
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
        TransitLandApi.retrieveFeeds(operators,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(feeds: List<Feed>) {
        // Need to filter out empty current feed version since they do not have
        // any feed version for the operator
        Timber.d("FEED: Filtering ${feeds.count()} operator feeds...")
        val filteredFeeds = feeds.filter { it.currentFeedVersion.isNotEmpty() }
        Timber.d("FEED: Saving ${filteredFeeds.count()} operator feeds...")
        feedDao.dbInsert {
            nuke()
            insert(filteredFeeds)
        }.subscribe({ _ ->
            onSaved(filteredFeeds)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(feeds: List<Feed>) {
        Timber.d("FEED: Successfully saved ${feeds.count()} operator feeds")
        this.onSuccess(feeds)
    }
}
