package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.rule.SynchronousTestRule
import com.rodolfonavalon.canadatransit.util.CustomMockWebServer
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
open class BaseMockServerTest {

    @get:Rule
    val synchronousTasks = SynchronousTestRule()

    companion object {
        lateinit var server: CustomMockWebServer

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            server = CustomMockWebServer()
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
    }

    @After
    open fun teardown(){
        // Check the server that all of the responses are consumed
        server.check()
    }
}