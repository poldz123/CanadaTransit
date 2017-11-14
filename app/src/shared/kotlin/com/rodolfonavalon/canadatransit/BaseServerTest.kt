package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.util.CustomMockWebServer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

/**
 * Base class for testing with the mock server. This is mainly used for
 * networking test cases to have a trampoline scheduler that will block
 * threading.
 *
 * This also handles assertion if ever some of the request has not been
 * consumed by the test case, also triggers to start and stop the mock
 * server before and after the test suite.
 *
 * IMPORTANT: That during [setup] the URL endpoint from the mock server
 * should be used by the API class as the base URL.
 */
open class BaseServerTest {

    companion object {
        @JvmField
        val server: CustomMockWebServer = CustomMockWebServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            server.stop()
        }
    }

    @Before
    open fun setup() {
        // Reset the server per test cases, this
        // to have a clean slate on it
        server.reset()
        // Restore the plugins to be synchronize
        // and wont run on different threads
        restorePlugins()
    }

    @After
    open fun teardown(){
        // Reset the plugins after each test cases to restore
        // it to being non-synchronous again
        resetPlugins()
        // Check the server that all of the responses are consumed
        server.check()
    }

    fun restorePlugins() {
        RxJavaPlugins.setNewThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
    }

    fun resetPlugins() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}