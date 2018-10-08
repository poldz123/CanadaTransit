package com.rodolfonavalon.canadatransit.transit

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.BaseMockServerTest
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.util.TestResourceModel
import com.rodolfonavalon.canadatransit.util.TestResourceModel.FeedModel.assertFeeds
import com.rodolfonavalon.canadatransit.util.TestResourceModel.FeedVersionModel.assertFeedVersion
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TransitLandApiTest : BaseMockServerTest() {

    override fun setup() {
        super.setup()
        TransitLandApi.initializeRetrofit(server.url("/api/v1/").toString())
    }

    @Test
    fun testTransitLandApi_disposable() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        synchronousTasks.enableTestSchedulerTasks()

        // Adds a delay to the response to properly test the activity lifecycle of the mock-server
        server.addResponseBody("/api/v1/operators", "{}")
        server.addResponseBody("/api/v1/feeds", "{}")
        server.addResponseBody("/api/v1/feed_versions/test", "{}")

        val mockOperator = mock(Operator::class.java)
        given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf("test"))
        val mockFeed = mock(Feed::class.java)
        given(mockFeed.activeFeedVersion).willReturn("test")

        val disposables = mutableListOf(
                TransitLandApi.retrieveOperators({ _ -> }, { _ -> }, activity),
                TransitLandApi.retrieveFeeds(mockOperator, { _ -> }, { _ -> }, activity),
                TransitLandApi.retrieveFeedVersion(mockFeed, { _ -> }, { _ -> }, activity)
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

        // Lets clean the mock web response, since the api call has already been disposed when
        // the activity's on-destroy is called above. This prevent error about unconsumed responses.
        server.clean()
    }

    @Test
    fun testRetrieveOperators_modelConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        var success = false
        var operators: List<Operator>? = null

        TransitLandApi.retrieveOperators({
            operators = it
            success = true
        }, { _ ->
            operators = null
            success = false
        })

        assertTrue(success)
        assertNotNull(operators)
        assertTrue(operators!!.isNotEmpty())
    }

    @Test
    fun testRetrieveOperators_dataConsistency() {
        val operators: List<Operator> = mutableListOf(
                TestResourceModel.OperatorModel.createOCTranspoModel(),
                TestResourceModel.OperatorModel.createAMTTranspoModel()
        )

        for (operator in operators) {
            server.addResponsePath("/api/v1/operators", "/transitland/operator-(${operator.operatorOneStopId})")
            var testOperators: List<Operator> = mutableListOf()
            var testOperatorError: Throwable? = null

            TransitLandApi.retrieveOperators({
                testOperators = it
            }, { error ->
                testOperatorError = error
            })

            assertNull(testOperatorError, "Error has occurred when retrieving operator feeds: $testOperatorError")
            assertEquals(1, testOperators.count())
        }
    }

    @Test
    fun testRetrieveFeed_modelConsistency() {
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page1")
        server.addResponsePath("/api/v1/feeds", "/transitland/feeds-page2")

        val mockOperator = mock(Operator::class.java)
        given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf("test"))
        var success = false
        var feeds: List<Feed>? = null

        TransitLandApi.retrieveFeeds(mockOperator, {
            feeds = it
            success = true
        }, {
            feeds = null
            success = false
        })

        assertTrue(success)
        assertNotNull(feeds)
        assertTrue(feeds!!.isNotEmpty())
    }

    @Test
    fun testRetrieveFeed_dataConsistency() {
        val feeds: List<Feed> = mutableListOf(
                TestResourceModel.FeedModel.createOCTranspoModel(),
                TestResourceModel.FeedModel.createAMTTranspoModel()
        )

        for (feed in feeds) {
            val mockOperator = mock(Operator::class.java)
            given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf(feed.feedOneStopId))

            server.addResponsePath("/api/v1/feeds", "/transitland/feed-(${feed.feedOneStopId})")
            var testFeeds: List<Feed> = mutableListOf()
            var testFeedError: Throwable? = null

            TransitLandApi.retrieveFeeds(mockOperator, { apiFeeds ->
                testFeeds = apiFeeds
            }, { error ->
                testFeedError = error
            })

            assertNull(testFeedError, "Error has occurred when retrieving operator feeds: $testFeedError")
            assertEquals(1, testFeeds.count())
            assertFeeds(testFeeds, feed)
        }
    }

    @Test
    fun testRetrieveFeedVersion_modelConsistency() {
        server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-versions-page1")
        server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-versions-page2")

        val mockFeed = mock(Feed::class.java)
        given(mockFeed.activeFeedVersion).willReturn("test")
        var success = false
        var feeds: List<FeedVersion>? = null

        TransitLandApi.retrieveFeedVersion(mockFeed, {
            feeds = it
            success = true
        }, {
            feeds = null
            success = false
        })

        assertTrue(success)
        assertNotNull(feeds)
        assertTrue(feeds!!.isNotEmpty())
    }

    @Test
    fun testRetrieveFeedVersion_dataConsistency() {
        val feedVersions: List<FeedVersion> = mutableListOf(
                TestResourceModel.FeedVersionModel.createOCTranspoModel(),
                TestResourceModel.FeedVersionModel.createAMTTranspoModel()
        )

        for (feedVersion in feedVersions) {
            val mockFeed = mock(Feed::class.java)
            given(mockFeed.activeFeedVersion).willReturn(feedVersion.sha1)

            server.addResponsePath("/api/v1/feed_versions", "/transitland/feed-version-(${feedVersion.feedOneStopId})")
            var testFeedVersions: List<FeedVersion>? = null
            var testFeedVersionError: Throwable? = null

            TransitLandApi.retrieveFeedVersion(mockFeed, { apiFeedVersion ->
                testFeedVersions = apiFeedVersion
            }, { error ->
                testFeedVersionError = error
            })

            assertNull(testFeedVersionError, "Error has occurred when retrieving operator feed version: $testFeedVersionError")
            for (testFeedVersion in testFeedVersions!!) {
                assertFeedVersion(testFeedVersion, feedVersion)
            }
        }
    }
}

