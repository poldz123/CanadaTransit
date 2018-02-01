package com.rodolfonavalon.canadatransit.transit

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.BaseServerTest
import com.rodolfonavalon.canadatransit.BuildConfig
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE,
        constants = BuildConfig::class,
        sdk = intArrayOf(26))
class TransitLandApiTest : BaseServerTest() {

    override fun setup() {
        super.setup()
        TransitLandApi.apiTestUrl = server.url("/api/v1/").toString()
    }

    @Test
    fun testActivityLifecycle() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        // Lets reset the plugin to prevent any blocking operation that will
        // result wait without us testing the life cycle
        resetPlugins()
        // Adds a delay to the response to properly test the activity lifecycle
        server.addResponseBody("/api/v1/operators", "{}", delay = 100)
        server.addResponseBody("/api/v1/feeds/test", "{}", delay = 100)
        server.addResponseBody("/api/v1/feed_versions/test", "{}", delay = 100)

        val mockOperator = mock(Operator::class.java)
        given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf("test"))
        val mockOperatorFeed = mock(OperatorFeed::class.java)
        given(mockOperatorFeed.activeFeedVersion).willReturn("test")

        val disposables = mutableListOf(
                TransitLandApi.retrieveOperators({ _ -> }, { _ -> }, activity),
                TransitLandApi.retrieveOperatorFeed(mockOperator, { _ -> }, { _ -> }, activity),
                TransitLandApi.retrieveOperatorFeedVersion(mockOperatorFeed, { _ -> }, { _ -> }, activity)
        )

        controller.create()
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.start()
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.resume()
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.pause()
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.stop()
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.saveInstanceState(Bundle())
        disposables.forEach { disposable -> assertFalse(disposable.isDisposed) }
        controller.destroy()
        disposables.forEach { disposable -> assertTrue(disposable.isDisposed) }

        // Lets clean the responses, since it can trigger the assertion
        // when server is checked.
        server.clean()
    }

    @Test
    fun testRetrieveOperators() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        var assertOperators: List<Operator> = mutableListOf()
        var assertError: Throwable? = null

        TransitLandApi.retrieveOperators({ operators ->
            assertOperators = operators
        }, { error ->
            assertError = error
        })

        assertNull(assertError, "Error has occurred when retrieving operators: $assertError")
        assertOperators(assertOperators, "o-f24-octranspo",
                "CA", "Ottawa", "CA-ON", "OC Transpo", 1)
        assertOperators(assertOperators, "o-f25-agencemtropolitainedetransport",
                "CA", null, "CA-QC", "Agence métropolitaine de transport", 2)
    }

    @Test
    fun testRetrieveOperatorFeed() {
        val operatorFeedData: List<Pair<String, String>> = mutableListOf(
                Pair("f-f24-octranspo", "o-f24-octranspo"),
                Pair("f-f25d-agencemtropolitainedetransportexpress", "o-f25-agencemtropolitainedetransport")
        )

        for ((oneStopId, operatorId) in operatorFeedData) {
            val mockOperator = mock(Operator::class.java)
            given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf(oneStopId))

            server.addResponsePath("/api/v1/feeds/$oneStopId", "/transitland/operator-feed-($oneStopId)")
            var assertOperatorFeeds: List<OperatorFeed> = mutableListOf()
            var assertOperatorFeedError: Throwable? = null

            TransitLandApi.retrieveOperatorFeed(mockOperator, { operatorFeeds ->
                assertOperatorFeeds = operatorFeeds
            }, { error ->
                assertOperatorFeedError = error
            })

            assertNull(assertOperatorFeedError, "Error has occurred when retrieving operator feeds: $assertOperatorFeedError")
            assertEquals(assertOperatorFeeds.count(), 1)
            assertOperatorFeeds(assertOperatorFeeds, oneStopId, operatorId)
        }
    }

    @Test
    fun testRetrieveFeedVersion() {
        val operatorFeedVersionData: List<Pair<String, String>> = mutableListOf(
                Pair("f-f24-octranspo", "d157d50441cd64c50ec01a300da521a477aa03c4"),
                Pair("f-f25d-agencemtropolitainedetransportexpress", "1b99f0448fb3ba210ea1b669529d60eeb5699a9b")
        )

        for ((oneStopId, activeFeedVersion) in operatorFeedVersionData) {
            val mockOperatorFeed = mock(OperatorFeed::class.java)
            given(mockOperatorFeed.activeFeedVersion).willReturn(activeFeedVersion)

            server.addResponsePath("/api/v1/feed_versions/$activeFeedVersion", "/transitland/operator-feed-version-($oneStopId)")
            var assertOperatorFeedVersion: OperatorFeedVersion? = null
            var assertOperatorFeedVersionError: Throwable? = null

            TransitLandApi.retrieveOperatorFeedVersion(mockOperatorFeed, { operatorFeedVersion ->
                assertOperatorFeedVersion = operatorFeedVersion
            }, { error ->
                assertOperatorFeedVersionError = error
            })

            assertNull(assertOperatorFeedVersionError, "Error has occurred when retrieving operator feed version: $assertOperatorFeedVersionError")
            assertOperatorFeedVersion(assertOperatorFeedVersion!!, oneStopId, activeFeedVersion)
        }
    }

    private fun assertOperators(operators: List<Operator>, operatorId: String, country: String, metro: String?, state: String, name: String, feedIdCount: Int) {
        assertTrue(operators.isNotEmpty(), "Operators is empty")
        var foundTestOperator = false
        for (operator in operators) {
            assertFalse(operator.representedInFeedOneStopIds.contains(operator.onestopId),
                    "One stop id exist in feed: ${operator.onestopId}")
            assertEquals(operator.country, country)
            // Lets find the test operator
            if (operatorId == operator.onestopId) {
                assertEquals(operator.metro, metro)
                assertEquals(operator.state, state)
                assertEquals(operator.name, name)
                assertEquals(operator.representedInFeedOneStopIds.count(), feedIdCount)
                foundTestOperator = true
            }
        }
        assertTrue(foundTestOperator, "Test operator was not found for: $operatorId")
    }

    private fun assertOperatorFeeds(operatorFeeds: List<OperatorFeed>, oneStopId: String, operatorId: String) {
        assertTrue(operatorFeeds.isNotEmpty(), "Operator Feeds is empty")

        for (operatorFeed in operatorFeeds) {
            assertEquals(operatorFeed.feedOneStopId, oneStopId)
            for (operatorInFeed in operatorFeed.operatorsInFeed) {
                assertEquals(operatorInFeed.operatorOneStopId, operatorId)
                assertEquals(operatorFeed.feedOneStopId, oneStopId)
            }
        }
    }

    private fun assertOperatorFeedVersion(operatorFeedVersion: OperatorFeedVersion, oneStopId: String, activeFeedVersion: String) {
        assertEquals(operatorFeedVersion.sha1, activeFeedVersion)
        assertEquals(operatorFeedVersion.feedOneStopId, oneStopId)
    }
}

