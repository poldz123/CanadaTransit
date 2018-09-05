package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.util.AbstractUpdateTask
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DatabaseUtil
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import timber.log.Timber

class OperatorUpdaterTask(private val updateManager: UpdateManager, private val trackingId: String):
        AbstractUpdateTask(updateManager, trackingId) {

    override fun onStart() {
        DebugUtil.assertMainThread()
        this.disposable = TransitLandApi.retrieveOperators(::onOperatorsReceived, ::onError)
    }

    private fun onOperatorsReceived(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("No operators was found, this could mean that the API has a BUG.")
            updateManager.success(trackingId)
            return
        }

        val dao = CanadaTransitApplication.appDatabase.transitLandDao()
        Timber.d("Saving ${operators.count()} operator...")
        DatabaseUtil.insert({
            DebugUtil.assertWorkerThread()
            // Trigger to insert the operators in the background thread.
            dao.insertOperators(operators)
        }, ::onOperatorsSaved, ::onError)
    }

    private fun onOperatorsSaved(rowIds: List<Long>) {
        DebugUtil.assertTrue(rowIds.isNotEmpty(), "There are no operators being saved on a successful database transaction: $trackingId")
        Timber.d("Successfully saved ${rowIds.count()} operators")
        updateManager.success(trackingId)
    }
}