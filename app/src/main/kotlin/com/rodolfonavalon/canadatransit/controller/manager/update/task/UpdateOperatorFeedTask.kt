package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.extension.dbQuery
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import timber.log.Timber

class UpdateOperatorFeedTask(private val updateManager: UpdateManager) : AbstractObservableTask<List<OperatorFeed>>() {
    val operatorDao = CanadaTransitApplication.appDatabase.operatorDao()
    val operatorFeedDao = CanadaTransitApplication.appDatabase.operatorFeedDao()
    val userOperatorDao = CanadaTransitApplication.appDatabase.userOperatorsDao()

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Retrieving operator feeds...")
        val disposable = operatorDao.dbQuery { findOperatorsOfUser() }
                .subscribe(::onUserOperatorsFound, ::onError)
        this.disposables.add(disposable)
    }

    private fun onUserOperatorsFound(operators: List<Operator>) {
        this.disposables.add(TransitLandApi.retrieveOperatorFeed(operators, ::onOperatorFeedsReceived, ::onError))
    }

    private fun onOperatorFeedsReceived(operatorFeeds: List<OperatorFeed>) {
        if (operatorFeeds.isEmpty()) {
            Timber.d("No operator feeds was found, this could mean that the API has a BUG.")
            onOperatorFeedsSaved(operatorFeeds)
            return
        }
        Timber.d("Saving ${operatorFeeds.count()} operator feeds...")
        operatorFeedDao.dbInsert {
            insert(operatorFeeds)
        }.subscribe({ rowIds ->
            DebugUtil.assertTrue(rowIds.isNotEmpty(), "Failed to save operator feeds: $trackingId")
            onOperatorFeedsSaved(operatorFeeds)
        }, ::onError)
    }

    private fun onOperatorFeedsSaved(operatorFeeds: List<OperatorFeed>) {
        this.observable.onNext(operatorFeeds)
        this.observable.onComplete()
        updateManager.success()
    }
}
