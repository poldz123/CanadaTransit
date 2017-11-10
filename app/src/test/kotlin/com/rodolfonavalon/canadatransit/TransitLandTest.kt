package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import kotlin.test.*

class TransitLandTest: BaseTest() {

    override fun before() {
        super.before()
        TransitLandApi.apiTestUrl = server.url("/api/v1/").toString()
    }

    @Test
    fun testRetrieveOperators() {
        server.addResponse("/api/v1/operators", "/transitland/operators-page1")
        server.addResponse("/api/v1/operators", "/transitland/operators-page2")
        var assertOperators: List<Operator> = mutableListOf()
        var assertError: Throwable? = null

        TransitLandApi.retrieveOperators({ operators ->
            assertOperators = operators
        }, { error ->
            assertError = error
        })

        server.takeRequest()
        server.takeRequest()

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
            val mockOctranspoOperator = mock(Operator::class.java)
            given(mockOctranspoOperator.representedInFeedOneStopIds).willReturn(mutableListOf(oneStopId))

            server.addResponse("/api/v1/feeds/$oneStopId", "/transitland/operator-feed-($oneStopId)")
            var assertOcTranspoOperatorFeeds: List<OperatorFeed> = mutableListOf()
            var assertOcTranspoOperatorFeedError: Throwable? = null

            TransitLandApi.retrieveOperatorFeed(mockOctranspoOperator, { operatorFeeds ->
                assertOcTranspoOperatorFeeds = operatorFeeds
            }, { error ->
                assertOcTranspoOperatorFeedError = error
            })

            server.takeRequest()
            assertNull(assertOcTranspoOperatorFeedError, "Error has occurred when retrieving operator feeds: $assertOcTranspoOperatorFeedError")
            assertEquals(assertOcTranspoOperatorFeeds.count(), 1)
            assertOperatorFeeds(assertOcTranspoOperatorFeeds, oneStopId, operatorId)
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
}

