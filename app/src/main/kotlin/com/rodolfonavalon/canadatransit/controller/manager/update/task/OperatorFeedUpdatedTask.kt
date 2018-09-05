package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.disposables.Disposable
import timber.log.Timber

class OperatorFeedUpdatedTask(private val updateManager: UpdateManager, private val trackingId: String): UpdateTask {
    var disposable: Disposable? = null

    override fun onStart() {
        this.disposable = TransitLandApi.retrieveOperators(::onOperatorsReceived, ::onError)
    }

    override fun onError(error: Throwable) {
        Timber.e(error, "Download entity has FAILED")
        onCancel()
        updateManager.failure(trackingId)
    }

    override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("OperatorFeedUpdatedTask has been CANCELLED")
        // Dispose the retrofit call
        disposable?.dispose()
    }

    private fun onOperatorsReceived(operators: List<Operator>) {

    }
}