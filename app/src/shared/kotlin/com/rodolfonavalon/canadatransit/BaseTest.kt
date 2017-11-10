package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.util.CustomMockWebServer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        constants = BuildConfig::class,
        minSdk = 21)
open class BaseTest {
    val server: CustomMockWebServer = CustomMockWebServer()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            RxJavaPlugins.setComputationSchedulerHandler { _ -> Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
            RxJavaPlugins.setNewThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }

    @Before
    open fun before() {
        server.start()
    }

    @After
    open fun after(){
        server.stop()
    }
}