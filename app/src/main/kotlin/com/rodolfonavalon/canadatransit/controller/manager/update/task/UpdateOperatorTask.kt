package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.extension.dbInsert
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import timber.log.Timber

class UpdateOperatorTask(private val updateManager: UpdateManager) : AbstractObservableTask<List<Operator>>() {

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Retrieving operators...")
        this.disposables.add(TransitLandApi.retrieveOperators(::onOperatorsReceived, ::onError))
    }

    private fun onOperatorsReceived(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("No operators was found, this could mean that the API has a BUG.")
            onOperatorsSaved(operators)
            return
        }
        Timber.d("Saving ${operators.count()} operators...")
        val dao = CanadaTransitApplication.appDatabase.operatorDao()
        dao.dbInsert {
            insert(operators)
        }.subscribe({ rowIds ->
            DebugUtil.assertTrue(rowIds.isNotEmpty(), "Failed to save operators: $trackingId")
            onOperatorsSaved(operators)
        }, ::onError)
    }

    private fun onOperatorsSaved(operators: List<Operator>) {
        Timber.d("Successfully saved ${operators.count()} operators")
        this.observable.onNext(operators)
        this.observable.onComplete()
        updateManager.success()
    }
}
