package com.rodolfonavalon.canadatransit.transit

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.BaseMockServerTest
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.util.TestResourceModel
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
                TransitLandApi.retrieveOperatorFeedVersion(mockFeed, { _ -> }, { _ -> }, activity)
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
    fun testRetrieveOperators_dataConsistency() {
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page1")
        server.addResponsePath("/api/v1/operators", "/transitland/operators-page2")
        var testOperators: List<Operator> = mutableListOf()
        var testOperatorError: Throwable? = null

        TransitLandApi.retrieveOperators({ operators ->
            testOperators = operators
        }, { error ->
            testOperatorError = error
        })

        val operatorOCTranspo = TestResourceModel.OperatorModel.createOCTranspoModel()
        val operatorAMTTranspo= TestResourceModel.OperatorModel.createAMTTranspoModel()

        assertNull(testOperatorError, "Error has occurred when retrieving operators: $testOperatorError")
        assertOperator(testOperators, operatorOCTranspo)
        assertOperator(testOperators, operatorAMTTranspo)
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

            server.addResponsePath("/api/v1/feeds", "/transitland/operator-feed-(${feed.feedOneStopId})")
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
    fun testRetrieveFeedVersion_dataConsistency() {
        val operatorFeedVersions: List<OperatorFeedVersion> = mutableListOf(
                TestResourceModel.OperatorFeedVersionModel.createOCTranspoModel(),
                TestResourceModel.OperatorFeedVersionModel.createAMTTranspoModel()
        )

        for (operatorFeedVersion in operatorFeedVersions) {
            val mockFeed = mock(Feed::class.java)
            given(mockFeed.activeFeedVersion).willReturn(operatorFeedVersion.sha1)

            server.addResponsePath("/api/v1/feed_versions", "/transitland/operator-feed-version-(${operatorFeedVersion.feedOneStopId})")
            var testOperatorFeedVersions: List<OperatorFeedVersion>? = null
            var testOperatorFeedVersionError: Throwable? = null

            TransitLandApi.retrieveOperatorFeedVersion(mockFeed, { apiOperatorFeedVersion ->
                testOperatorFeedVersions = apiOperatorFeedVersion
            }, { error ->
                testOperatorFeedVersionError = error
            })

            assertNull(testOperatorFeedVersionError, "Error has occurred when retrieving operator feed version: $testOperatorFeedVersionError")
            for (testOperatorFeedVersion in testOperatorFeedVersions!!) {
                assertOperatorFeedVersion(testOperatorFeedVersion, operatorFeedVersion)
            }
        }
    }

    private fun assertOperator(actualOperators: List<Operator>, expectedOperator: Operator) {
        assertTrue(actualOperators.isNotEmpty(), "Operators is empty")
        for (actualOperator in actualOperators) {
            // All operator should have the same country
            assertEquals(expectedOperator.country, actualOperator.country)
            // Lets find the test operator
            if (expectedOperator.operatorOneStopId == actualOperator.operatorOneStopId) {
                assertEquals(expectedOperator.name, actualOperator.name)
                assertEquals(expectedOperator.state, actualOperator.state)
                assertEquals(expectedOperator.timezone, actualOperator.timezone)
                assertEquals(expectedOperator.createdAt, actualOperator.createdAt)
                assertEquals(expectedOperator.updatedAt, actualOperator.updatedAt)
                assertEquals(expectedOperator.website, actualOperator.website)
                assertEquals(expectedOperator.metro, actualOperator.metro)
                assertEquals(expectedOperator.shortName, actualOperator.shortName)
                assertEquals(expectedOperator.representedInFeedOneStopIds.count(), actualOperator.representedInFeedOneStopIds.count())
                for (i in 0 until expectedOperator.representedInFeedOneStopIds.count()) {
                    assertEquals(expectedOperator.representedInFeedOneStopIds[i], actualOperator.representedInFeedOneStopIds[i])
                }
                return
            }
        }
        fail("Test operator was not found for: ${expectedOperator.operatorOneStopId}")
    }

    private fun assertFeeds(actualFeeds: List<Feed>, expectedFeed: Feed) {
        assertTrue(actualFeeds.isNotEmpty(), "Operator Feeds is empty")

        for (actualOperatorFeed in actualFeeds) {
            if (actualOperatorFeed.feedOneStopId == expectedFeed.feedOneStopId) {
                assertEquals(expectedFeed.operatorOneStopId, actualOperatorFeed.operatorOneStopId)
                assertEquals(expectedFeed.name, actualOperatorFeed.name)
                assertEquals(expectedFeed.createdAt, actualOperatorFeed.createdAt)
                assertEquals(expectedFeed.updatedAt, actualOperatorFeed.updatedAt)
                assertEquals(expectedFeed.url, actualOperatorFeed.url)
                assertEquals(expectedFeed.feedFormat, actualOperatorFeed.feedFormat)
                assertEquals(expectedFeed.lastFetchAt, actualOperatorFeed.lastFetchAt)
                assertEquals(expectedFeed.lastImportedAt, actualOperatorFeed.lastImportedAt)
                assertEquals(expectedFeed.importStatus, actualOperatorFeed.importStatus)
                assertEquals(expectedFeed.activeFeedVersion, actualOperatorFeed.activeFeedVersion)
                assertEquals(expectedFeed.feedVersionUrl, actualOperatorFeed.feedVersionUrl)
                return
            }
        }
        fail("Test operator feed was not found for: ${expectedFeed.feedOneStopId}")
    }

    private fun assertOperatorFeedVersion(actualOperatorFeedVersion: OperatorFeedVersion, expectedOperatorFeedVersion: OperatorFeedVersion) {
        assertEquals(expectedOperatorFeedVersion.sha1, actualOperatorFeedVersion.sha1)
        assertEquals(expectedOperatorFeedVersion.feedOneStopId, actualOperatorFeedVersion.feedOneStopId)
        assertEquals(expectedOperatorFeedVersion.earliestCalendarDate, actualOperatorFeedVersion.earliestCalendarDate)
        assertEquals(expectedOperatorFeedVersion.latestCalendarDate, actualOperatorFeedVersion.latestCalendarDate)
        assertEquals(expectedOperatorFeedVersion.md5, actualOperatorFeedVersion.md5)
        assertEquals(expectedOperatorFeedVersion.fetchedAt, actualOperatorFeedVersion.fetchedAt)
        assertEquals(expectedOperatorFeedVersion.importedAt, actualOperatorFeedVersion.importedAt)
        assertEquals(expectedOperatorFeedVersion.createdAt, actualOperatorFeedVersion.createdAt)
        assertEquals(expectedOperatorFeedVersion.updatedAt, actualOperatorFeedVersion.updatedAt)
        assertEquals(expectedOperatorFeedVersion.importStatus, actualOperatorFeedVersion.importStatus)
        assertEquals(expectedOperatorFeedVersion.url, actualOperatorFeedVersion.url)
        assertEquals(expectedOperatorFeedVersion.downloadUrl, actualOperatorFeedVersion.downloadUrl)
        assertEquals(expectedOperatorFeedVersion.importLevel, actualOperatorFeedVersion.importLevel)
        assertEquals(expectedOperatorFeedVersion.isActiveFeedVersion, actualOperatorFeedVersion.isActiveFeedVersion)

    }
}

