package com.rodolfonavalon.canadatransit.controller.manager.update.util

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.queue.OnFailureTaskListener
import com.rodolfonavalon.canadatransit.controller.util.queue.OnSuccessTaskListener
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber

abstract class AbstractUpdateTask(val updateManager: UpdateManager): UpdateTask {
    var disposable: Disposable? = null

    lateinit var trackingId: String
    lateinit var callback: Observer<Any>

    override fun onStart(trackingId: String, callbackObserver: Observer<Any>) {
        DebugUtil.assertMainThread()
        this.trackingId = trackingId
        this.callback = callbackObserver
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
        // Trigger a failure task within the manager
        updateManager.failure()
    }
}
