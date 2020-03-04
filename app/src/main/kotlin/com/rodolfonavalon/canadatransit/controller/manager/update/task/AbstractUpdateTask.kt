package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import timber.log.Timber

abstract class AbstractUpdateTask<T : Any> : AbstractObservableTask<T>() {
    private val updateManager = UpdateManager.manager() as UpdateManager

    final override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("Task has been CANCELLED: $trackingId")
        // Dispose the retrofit call
        disposables.dispose()
    }

    final override fun onError(error: Throwable) {
        DebugUtil.assertMainThread()
        Timber.e(error, "Task has FAILED: $trackingId")
        observable.onError(error)
        updateManager.failure()
    }

    fun onSuccess(result: T) {
        this.observable.onNext(result)
        this.observable.onComplete()
        updateManager.success()
    }
}
