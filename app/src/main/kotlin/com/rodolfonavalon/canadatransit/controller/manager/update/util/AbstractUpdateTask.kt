package com.rodolfonavalon.canadatransit.controller.manager.update.util

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.queue.OnFailureTaskListener
import com.rodolfonavalon.canadatransit.controller.util.queue.OnSuccessTaskListener
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class AbstractUpdateTask(val updateManager: UpdateManager,
                                  val onSuccess: OnSuccessTaskListener<List<Operator>>? = null,
                                  private val onFailure: OnFailureTaskListener? = null): UpdateTask {
    var disposable: Disposable? = null
    var trackingId: String = ""

    override fun onStart(trackingId: String) {
        DebugUtil.assertMainThread()
        this.trackingId = trackingId
    }

    override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("AbstractUpdateTask has been CANCELLED: $trackingId")
        // Dispose the retrofit call
        disposable?.dispose()
    }

    override fun onError(error: Throwable) {
        DebugUtil.assertMainThread()
        Timber.e(error, "AbstractUpdateTask has FAILED: $trackingId")
        // Error means that we are cancelling the task
        onCancel()
        // Trigger the failure callback to the caller
        onFailure?.invoke()
        // Trigger a failure task within the manager
        updateManager.failure()
    }
}
