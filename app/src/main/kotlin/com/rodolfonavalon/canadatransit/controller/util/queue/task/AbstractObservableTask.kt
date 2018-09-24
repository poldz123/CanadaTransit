package com.rodolfonavalon.canadatransit.controller.util.queue.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber

abstract class AbstractObservableTask<T: Any> : Maybe<T>(), UpdateTask {
    val observable: ReplaySubject<T> = ReplaySubject.create(1)
    val disposables: CompositeDisposable = CompositeDisposable()
    lateinit var trackingId: String

    final override fun subscribeActual(observer: MaybeObserver<in T>?) {
        observable.subscribeBy(
                onNext = { observer?.onSuccess(it) },
                onError = { observer?.onError(it) }
        )
    }

    override fun onStart(trackingId: String) {
        DebugUtil.assertMainThread()
        this.trackingId = trackingId
    }

    final override fun onCancel() {
        DebugUtil.assertMainThread()
        Timber.d("AbstractUpdateTask has been CANCELLED: $trackingId")
        // Dispose the retrofit call
        disposables.dispose()
    }

    final override fun onError(error: Throwable) {
        DebugUtil.assertMainThread()
        Timber.e(error, "AbstractUpdateTask has FAILED: $trackingId")
        observable.onError(error)
    }
}