package com.rodolfonavalon.canadatransit.controller.util.queue.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateTask
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class AbstractObservableTask<T : Any> : Single<T>(), UpdateTask {
    val observable: ReplaySubject<T> = ReplaySubject.create(1)
    val disposables: CompositeDisposable = CompositeDisposable()
    lateinit var trackingId: String

    override fun subscribeActual(observer: SingleObserver<in T>) {
        observable.singleOrError().subscribe(observer)
    }

    override fun onStart(trackingId: String) {
        DebugUtil.assertMainThread()
        this.trackingId = trackingId
    }
}
