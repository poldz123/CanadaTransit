package com.rodolfonavalon.canadatransit.controller.manager.update.util

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class AbstractUpdateTask(private val updateManager: UpdateManager, private val trackingId: String): UpdateTask {
    var disposable: Disposable? = null

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
        // Trigger a failure task within the manager
        updateManager.failure(trackingId)
    }
}