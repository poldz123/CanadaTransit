package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.extension.dbUpdate
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import io.reactivex.rxkotlin.addTo
import org.joda.time.DateTime
import timber.log.Timber

class UpdateFeedVersionTask : AbstractUpdateTask<List<FeedVersion>>() {
    private val userTransitDao = CanadaTransitApplication.appDatabase.userTransitDao()
    private val feedVersionDao = CanadaTransitApplication.appDatabase.feedVersionDao()

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
        TransitLandApi.retrieveFeedVersion(feeds,
                ::onReceived,
                this::onError)
                .addTo(this.disposables)
    }

    private fun onReceived(feedVersions: List<FeedVersion>) {
        Timber.d("VERSION: Saving ${feedVersions.count()} operator feed versions...")
        feedVersionDao.dbInsert {
            insert(feedVersions)
        }.subscribe({ _ ->
            // Once Operator feed version was successfully saved it should also
            // update all of the user operator's updated-at.
            userTransitDao.dbUpdate {
                updateAll(DateTime.now())
            }.subscribe({ _ ->
                onSaved(feedVersions)
            }, this::onError)
        }, this::onError).addTo(this.disposables)
    }

    private fun onSaved(feedVersions: List<FeedVersion>) {
        Timber.d("VERSION: Successfully saved ${feedVersions.count()} operator feed versions")
        this.onSuccess(feedVersions)
    }
}