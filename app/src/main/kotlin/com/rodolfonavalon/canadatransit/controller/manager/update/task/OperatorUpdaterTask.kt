package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.OnFailureTaskListener
import com.rodolfonavalon.canadatransit.controller.manager.update.OnSuccessTaskListener
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.util.AbstractUpdateTask
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DatabaseUtil
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import timber.log.Timber

class OperatorUpdaterTask(updateManager: UpdateManager,
                          onSuccess: OnSuccessTaskListener<List<Operator>>? = null,
                          onFailure: OnFailureTaskListener? = null):
        AbstractUpdateTask(updateManager, onSuccess, onFailure) {

    override fun onStart(trackingId: String) {
        super.onStart(trackingId)
        Timber.d("Retrieving operators...")
        this.disposable = TransitLandApi.retrieveOperators(::onOperatorsReceived, ::onError)
    }

    private fun onOperatorsReceived(operators: List<Operator>) {
        if (operators.isEmpty()) {
            Timber.d("No operators was found, this could mean that the API has a BUG.")
            onOperatorsSaved(operators)
            return
        }

        val dao = CanadaTransitApplication.appDatabase.operatorDao()

        Timber.d("Saving ${operators.count()} operators...")
        DatabaseUtil.insert({
             // Trigger to insert the operators in the background thread.
            dao.insert(operators)
        }, { rowIds ->
            DebugUtil.assertTrue(rowIds.isNotEmpty(), "There are no operators being saved on a successful database transaction: $trackingId")
            onOperatorsSaved(operators)
        }, ::onError)
    }

    private fun onOperatorsSaved(operators: List<Operator>) {
        Timber.d("Successfully saved ${operators.count()} operators")
        this.onSuccess?.invoke(operators)
        this.updateManager.success()
    }
}
