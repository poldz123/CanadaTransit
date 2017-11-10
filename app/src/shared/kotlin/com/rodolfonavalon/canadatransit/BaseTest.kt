package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.util.CustomMockWebServer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

open class BaseTest {

    companion object {
        @JvmField
        val server: CustomMockWebServer = CustomMockWebServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            server.start()
            RxJavaPlugins.setNewThreadSchedulerHandler { _ -> Schedulers.trampoline() }
            RxJavaPlugins.setComputationSchedulerHandler { _ -> Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
            RxAndroidPlugins.setMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            server.stop()
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }

    @Before
    open fun setup() {
    }

    @After
    open fun teardown(){
    }
}