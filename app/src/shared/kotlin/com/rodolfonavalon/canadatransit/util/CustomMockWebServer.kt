package com.rodolfonavalon.canadatransit.util

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.URI
import java.util.*

class CustomMockWebServer {
    private val server: MockWebServer by lazy {
        val server = MockWebServer()
        server.setDispatcher(MockWebServerDispatcher())
        server
    }

    private var responses: HashMap<String, Queue<MockWebServerResponse>> = hashMapOf()

    fun addResponse(path: String, responseCode: Int = 200) {
        addResponse(path, MockWebServerResponse(null, responseCode))
    }

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

    fun start() {
        // Clear the previous response in the queue
        responses.clear()
        // Start the mock server
        server.start()
    }

    fun stop() {
        // Stop the mock server
        server.shutdown()
        // All of the response should be consumed
        if (responses.isNotEmpty()) {
            // TODO: print all of the responses that was not consumed
            throw AssertionError("Stopping the server without consuming all responses")
        }
    }

    fun url(path: String): HttpUrl {
        return server.url(path)
    }

    fun takeRequest() {
        server.takeRequest()
    }

    private data class MockWebServerResponse(val response: String?, val code: Int)

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