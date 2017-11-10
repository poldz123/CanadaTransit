package com.rodolfonavalon.canadatransit.util

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.URI
import java.util.*

/**
 * This is the wrapper of the [MockWebServer] that handles the request and response
 * of the server and also the proper assertion if all of the request are consumed.
 */
class CustomMockWebServer {
    private val server: MockWebServer by lazy {
        val server = MockWebServer()
        server.setDispatcher(MockWebServerDispatcher())
        server
    }

    private var responses: HashMap<String, Queue<MockWebServerResponse>> = hashMapOf()

    /**
     * Adds the response of the mock server for the request.
     *
     * @param path The api path to the request
     * @param responseCode The response code of the request
     */
    fun addResponse(path: String, responseCode: Int = 200) {
        addResponse(path, MockWebServerResponse(null, responseCode))
    }

    /**
     * Adds the response of the mock server for the request.
     *
     * @param path The api path to the request
     * @param filePath The path of the response body of the request
     * @param responseCode The response code of the request
     */
    fun addResponse(path: String, filePath: String, responseCode: Int = 200) {
        val body = javaClass.getResource(filePath).readText()
        addResponse(path, MockWebServerResponse(body, responseCode))
    }

    private fun addResponse(path: String, response: MockWebServerResponse) {
        if (!responses.containsKey(path)) {
            // Initialize the queue of responses
            responses[path] = ArrayDeque()
        }
        // Attach the response to the queue
        responses[path]!!.add(response)
    }

    private fun removeResponse(path: String): MockWebServerResponse {
        if (!responses.containsKey(path) || responses[path] == null) {
            throw AssertionError("Removing without a response for path: $path")
        }
        // Retrieve the queue
        val serverResponses = responses[path]!!
        // Retrieve and remove the response
        val response = serverResponses.poll()
        // Remove response when all of the response are consumed
        if (serverResponses.isEmpty()) {
            responses.remove(path)
        }
        return response
    }

    /**
     * Starts the server and clearing the past responses.
     */
    fun start() {
        stop()
        // Start the mock server
        server.start()
    }

    /**
     * Stops the server and checks if all of the responses
     * are consumed by the network.
     *
     * @throws [AssertionError] If responses are not consumed
     */
    fun stop() {
        // Stop the mock server
        server.shutdown()
        // All of the response should be consumed
        if (responses.isNotEmpty()) {
            var errorMessage = "Stopping the server without consuming all responses: \n"
            for ((key, value) in responses) {
                errorMessage += "\n\t[$key] => [${value.count()}] requests"
            }
            throw AssertionError(errorMessage)
        }
    }

    /**
     * Returns a URL for connecting to this server.
     *
     * @param path the request path, such as "/".
     */
    fun url(path: String): HttpUrl {
        return server.url(path)
    }

    /**
     * Awaits the next HTTP request, removes it, and returns it. Callers should use this to verify the
     * request was sent as intended. This method will block until the request is available, possibly
     * forever.
     *
     * @return the head of the request queue
     */
    fun takeRequest(): RecordedRequest {
        return server.takeRequest()
    }

    /**
     * The response of the mock server for each request. Each request can return
     * a response body only when response code a 200 series.
     *
     * @property response The response string of the request
     * @property code The response code of the request
     */
    private data class MockWebServerResponse(val response: String?, val code: Int)

    /**
     * This is the mock server handler for each of the request that will retrieve the
     * responses that was attached. Each of the responses are removed if ever it was
     * successfully requested by the mock server.
     */
    private inner class MockWebServerDispatcher: Dispatcher() {

        override fun dispatch(request: RecordedRequest?): MockResponse {
            if (request != null) {
                // Parse the path without the query
                val uri = URI(request.path)
                val path = URI(uri.scheme, uri.authority, uri.path, null, uri.fragment).toString()
                // Retrieve the next response from the queue
                val response = removeResponse(path)
                // Create the response for the request
                val mockResponse = MockResponse()
                mockResponse.setBody(response.response)
                mockResponse.setResponseCode(response.code)
                return mockResponse
            }
            throw AssertionError("Unknown request from the mock server")
        }

    }
}