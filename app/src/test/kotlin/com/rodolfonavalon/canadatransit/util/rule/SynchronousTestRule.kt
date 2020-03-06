package com.rodolfonavalon.canadatransit.util.rule

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * This Rule is to make all of the asynchronous tasks use only a single thread. The networking
 * would be mostly benefit on this rule since this will work on both Retrofit and ReactiveX.
 */
class SynchronousTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                enableSynchronousTasks()
                try {
                    base.evaluate()
                } finally {
                    reset()
                }
            }
        }
    }

    /**
     * Creates a trampoline scheduler that would make all of the asynchronous tasks run
     * on a single thread.
     */
    fun enableSynchronousTasks() {
        RxJavaPlugins.setNewThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
    }

    /**
     * Creates a [TestScheduler] that enables controlling the asynchronous tasks when testing
     * it. Mostly used when tests are deterministic which single threaded task wont be able to do.
     */
    fun enableTestSchedulerTasks(): TestScheduler {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setNewThreadSchedulerHandler { _ -> testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { _ -> testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { _ -> testScheduler }
        return testScheduler
    }

    /**
     * Reset the plugins after each test cases to restore it to being non-synchronous again
     */
    fun reset() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}
