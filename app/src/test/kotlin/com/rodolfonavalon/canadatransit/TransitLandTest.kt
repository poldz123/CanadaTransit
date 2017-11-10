package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.Operator
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        assertOperators(assertOperators)
    }

    private fun assertOperators(operators: List<Operator>) {
        val testOCTranspoId = "o-f24-octranspo"
        val testCountry = "CA"
        val testMetro = "Ottawa"
        val testState = "CA-ON"
        val testName = "OC Transpo"

        assertTrue(operators.isNotEmpty(), "Operators is empty")
        var foundTestOperator = false
        for (operator in operators) {
            assertFalse(operator.representedInFeedOneStopIds.contains(operator.onestopId),
                    "One stop id exist in feed: ${operator.onestopId}")
            assertTrue(operator.country == testCountry)
            // Lets find the test operator
            if (testOCTranspoId == operator.onestopId) {
                assertTrue(operator.metro == testMetro)
                assertTrue(operator.state == testState)
                assertTrue(operator.name == testName)
                foundTestOperator = true
            }
        }
        assertTrue(foundTestOperator, "Test operator was not found for: $testOCTranspoId")
    }
}
