package com.rodolfonavalon.canadatransit.util

import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import org.junit.*

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
open class BaseMockServerTest : BaseTest() {

    companion object {
        lateinit var server: TransitMockWebServer

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            server = TransitMockWebServer()
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            server.stop()
        }
    }

    @Before
    override fun setup() {
        super.setup()
        // Reset the server per test cases, this
        // to have a clean slate on it
        server.reset()
        // Initialize the server path the test are connecting into
        TransitLandApi.initializeRetrofit(server.url("/api/v1/").toString())
    }

    @After
    override fun teardown() {
        super.teardown()
        // Check the server that all of the responses are consumed
        server.check()
    }
}
