package com.rodolfonavalon.canadatransit.transit

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.BaseMockServerTest
import com.rodolfonavalon.canadatransit.BuildConfig
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
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
    fun testRetrieveOperatorFeed_dataConsistency() {
        val operatorFeeds: List<OperatorFeed> = mutableListOf(
                TestResourceModel.OperatorFeedModel.createOCTranspoModel(),
                TestResourceModel.OperatorFeedModel.createAMTTranspoModel()
        )

        for (operatorFeed in operatorFeeds) {
            val mockOperator = mock(Operator::class.java)
            given(mockOperator.representedInFeedOneStopIds).willReturn(mutableListOf(operatorFeed.feedOneStopId))

            server.addResponsePath("/api/v1/feeds", "/transitland/operator-feed-(${operatorFeed.feedOneStopId})")
            var testOperatorFeeds: List<OperatorFeed> = mutableListOf()
            var testOperatorFeedError: Throwable? = null

            TransitLandApi.retrieveOperatorFeed(mockOperator, { apiOperatorFeeds ->
                testOperatorFeeds = apiOperatorFeeds
            }, { error ->
                testOperatorFeedError = error
            })

            assertNull(testOperatorFeedError, "Error has occurred when retrieving operator feeds: $testOperatorFeedError")
            assertEquals(1, testOperatorFeeds.count())
            assertOperatorFeeds(testOperatorFeeds, operatorFeed)
        }
    }

    @Test
    fun testRetrieveFeedVersion_dataConsistency() {
        val operatorFeedVersions: List<OperatorFeedVersion> = mutableListOf(
                TestResourceModel.OperatorFeedVersionModel.createOCTranspoModel(),
                TestResourceModel.OperatorFeedVersionModel.createAMTTranspoModel()
        )

        for (operatorFeedVersion in operatorFeedVersions) {
            val mockOperatorFeed = mock(OperatorFeed::class.java)
            given(mockOperatorFeed.activeFeedVersion).willReturn(operatorFeedVersion.sha1)

            server.addResponsePath("/api/v1/feed_versions/${operatorFeedVersion.sha1}", "/transitland/operator-feed-version-(${operatorFeedVersion.feedOneStopId})")
            var testOperatorFeedVersion: OperatorFeedVersion? = null
            var testOperatorFeedVersionError: Throwable? = null

            TransitLandApi.retrieveOperatorFeedVersion(mockOperatorFeed, { apiOperatorFeedVersion ->
                testOperatorFeedVersion = apiOperatorFeedVersion
            }, { error ->
                testOperatorFeedVersionError = error
            })

            assertNull(testOperatorFeedVersionError, "Error has occurred when retrieving operator feed version: $testOperatorFeedVersionError")
            assertOperatorFeedVersion(testOperatorFeedVersion!!, operatorFeedVersion)
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

    private fun assertOperatorFeeds(actualOperatorFeeds: List<OperatorFeed>, expectedOperatorFeed: OperatorFeed) {
        assertTrue(actualOperatorFeeds.isNotEmpty(), "Operator Feeds is empty")

        for (actualOperatorFeed in actualOperatorFeeds) {
            if (actualOperatorFeed.feedOneStopId == expectedOperatorFeed.feedOneStopId) {
                assertEquals(expectedOperatorFeed.operatorOneStopId, actualOperatorFeed.operatorOneStopId)
                assertEquals(expectedOperatorFeed.name, actualOperatorFeed.name)
                assertEquals(expectedOperatorFeed.createdAt, actualOperatorFeed.createdAt)
                assertEquals(expectedOperatorFeed.updatedAt, actualOperatorFeed.updatedAt)
                assertEquals(expectedOperatorFeed.url, actualOperatorFeed.url)
                assertEquals(expectedOperatorFeed.feedFormat, actualOperatorFeed.feedFormat)
                assertEquals(expectedOperatorFeed.lastFetchAt, actualOperatorFeed.lastFetchAt)
                assertEquals(expectedOperatorFeed.lastImportedAt, actualOperatorFeed.lastImportedAt)
                assertEquals(expectedOperatorFeed.importStatus, actualOperatorFeed.importStatus)
                assertEquals(expectedOperatorFeed.activeFeedVersion, actualOperatorFeed.activeFeedVersion)
                assertEquals(expectedOperatorFeed.feedVersionUrl, actualOperatorFeed.feedVersionUrl)
                return
            }
        }
        fail("Test operator feed was not found for: ${expectedOperatorFeed.feedOneStopId}")
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

