package com.rodolfonavalon.canadatransit.controller.manager.update.util

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class AbstractUpdateTask<T>(val updateManager: UpdateManager): Maybe<T>(), UpdateTask {
    var disposable: Disposable? = null
    var observer: MaybeObserver<in T>? = null

    lateinit var trackingId: String

    override fun subscribeActual(observer: MaybeObserver<in T>?) {
        this.observer = observer
    }

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
        // Trigger a failure task within the manager
        updateManager.failure()
    }
}
