package com.rodolfonavalon.canadatransit.util.generator

import com.rodolfonavalon.canadatransit.controller.database.converter.moshi.adapter.DateTimeAdapter
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.Tags
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import net.danlew.android.joda.DateUtils

object TestResourceModel {

    object OperatorModel {

        fun createRandomModel(index: Int): Operator {
            return Operator(
                    UUID.randomUUID().toString(),
                    "OC Transpo$index",
                    "CA-ON$index",
                    "America/Montreal",
                    DateTimeAdapter().fromJson("2016-05-26T21:32:08.877Z")!!,
                    "CA$index",
                    "http://www.octranspo.com$index",
                    "Ottawa$index",
                    null,
                    mutableListOf("f-f24-octranspo"),
                    Tags(null, null, null)
            )
        }

        fun createOCTranspoModel(): Operator {
            return Operator(
                    "o-f24-octranspo",
                    "OC Transpo",
                    "CA-ON",
                    "America/Montreal",
                    DateTimeAdapter().fromJson("2016-05-26T21:32:08.877Z")!!,
                    "CA",
                    "http://www.octranspo.com",
                    "Ottawa",
                    null,
                    mutableListOf("f-f24-octranspo"),
                    Tags(null, null, null)
            )
        }

        fun createAMTTranspoModel(): Operator {
            return Operator(
                    "o-f25-agencemtropolitainedetransport",
                    "Agence métropolitaine de transport",
                    "CA-QC",
                    "America/Montreal",
                    DateTimeAdapter().fromJson("2016-08-26T22:39:04.204Z")!!,
                    "CA",
                    "http://www.amt.qc.ca/",
                    null,
                    "AMT",
                    mutableListOf(
                            "f-f25d-agencemtropolitainedetransportexpress",
                            "f-f25-agencemtropolitainedetransport"
                    ),
                    Tags("TRAINS", "fr", null)
            )
        }

        fun assertOperator(actualOperators: List<Operator>, expectedOperator: Operator) {
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
                    assertEquals(expectedOperator.website, actualOperator.website)
                    assertEquals(expectedOperator.metro, actualOperator.metro)
                    assertEquals(expectedOperator.shortName, actualOperator.shortName)
                    assertEquals(expectedOperator.representedInFeedOneStopIds.count(), actualOperator.representedInFeedOneStopIds.count())
                    assertTrue(DateUtils.isToday(actualOperator.updatedAt))
                    for (i in 0 until expectedOperator.representedInFeedOneStopIds.count()) {
                        assertEquals(expectedOperator.representedInFeedOneStopIds[i], actualOperator.representedInFeedOneStopIds[i])
                    }
                    return
                }
            }
            fail("Test operator was not found for: ${expectedOperator.operatorOneStopId}")
        }
    }

    object FeedModel {

        fun createOCTranspoModel(): Feed {
            return Feed(
                    "f-f24-octranspo",
                    "o-f24-octranspo",
                    "1b99f0448fb3ba210ea1b669529d60eeb5699a9b",
                    "03ee676c71cdce519b0723f1a455bb838dc9ecf6",
                    null,
                    DateTimeAdapter().fromJson("2016-05-26T21:32:08.883Z")!!,
                    "http://www.octranspo1.com/files/google_transit.zip",
                    "gtfs",
                    "most_recent_succeeded",
                    "https://api.transit.land/api/v1/feed_versions?feed_onestop_id=f-f24-octranspo"
            )
        }

        fun createAMTTranspoModel(): Feed {
            return Feed(
                    "f-f25d-agencemtropolitainedetransportexpress",
                    "o-f25-agencemtropolitainedetransport",
                    "42e9b5c66882cd52af6e86669e5e14e57c7b1230",
                    "1b99f0448fb3ba210ea1b669529d60eeb5699a9b",
                    null,
                    DateTimeAdapter().fromJson("2016-08-26T23:08:40.138Z")!!,
                    "http://www.amt.qc.ca/xdata/express/google_transit.zip",
                    "gtfs",
                    "most_recent_succeeded",
                    "https://api.transit.land/api/v1/feed_versions?feed_onestop_id=f-f25d-agencemtropolitainedetransportexpress"
            )
        }

        fun assertFeeds(actualFeeds: List<Feed>, expectedFeed: Feed) {
            assertTrue(actualFeeds.isNotEmpty(), "Operator Feeds is empty")

            for (actualOperatorFeed in actualFeeds) {
                if (actualOperatorFeed.feedOneStopId == expectedFeed.feedOneStopId) {
                    assertEquals(expectedFeed.operatorOneStopId, actualOperatorFeed.operatorOneStopId)
                    assertEquals(expectedFeed.name, actualOperatorFeed.name)
                    assertEquals(expectedFeed.createdAt, actualOperatorFeed.createdAt)
                    assertEquals(expectedFeed.url, actualOperatorFeed.url)
                    assertEquals(expectedFeed.feedFormat, actualOperatorFeed.feedFormat)
                    assertEquals(expectedFeed.importStatus, actualOperatorFeed.importStatus)
                    assertEquals(expectedFeed.activeFeedVersion, actualOperatorFeed.activeFeedVersion)
                    assertEquals(expectedFeed.feedVersionUrl, actualOperatorFeed.feedVersionUrl)
                    assertTrue(DateUtils.isToday(actualOperatorFeed.updatedAt))
                    return
                }
            }
            fail("Test operator feed was not found for: ${expectedFeed.feedOneStopId}")
        }
    }

    object FeedVersionModel {

        fun createOCTranspoModel(): FeedVersion {
            return FeedVersion(
                    "d157d50441cd64c50ec01a300da521a477aa03c4",
                    "f-f24-octranspo",
                    DateTimeAdapter().fromJson("2017-10-27")!!,
                    DateTimeAdapter().fromJson("2017-12-23")!!,
                    "da348c4b21073a8d1cf52168d03e3e48",
                    DateTimeAdapter().fromJson("2017-10-27T21:30:17.347Z")!!,
                    DateTimeAdapter().fromJson("2017-10-27T21:30:17.354Z")!!,
                    "most_recent_succeeded",
                    "http://www.octranspo1.com/files/google_transit.zip",
                    "https://transitland-gtfs.s3.amazonaws.com/datastore-uploads/feed_version/d157d50441cd64c50ec01a300da521a477aa03c4.zip",
                    4,
                    false
            )
        }

        fun createAMTTranspoModel(): FeedVersion {
            return FeedVersion(
                    "1b99f0448fb3ba210ea1b669529d60eeb5699a9b",
                    "f-f25d-agencemtropolitainedetransportexpress",
                    DateTimeAdapter().fromJson("2017-01-02")!!,
                    DateTimeAdapter().fromJson("2017-07-01")!!,
                    "7d9249c414bc7007bde1712b17a5eb46",
                    DateTimeAdapter().fromJson("2017-06-22T17:30:28.106Z")!!,
                    DateTimeAdapter().fromJson("2017-06-22T17:30:28.098Z")!!,
                    "most_recent_succeeded",
                    "http://www.amt.qc.ca/xdata/express/google_transit.zip",
                    "https://transitland-gtfs.s3.amazonaws.com/datastore-uploads/feed_version/1b99f0448fb3ba210ea1b669529d60eeb5699a9b.zip",
                    4,
                    true
            )
        }

        fun assertFeedVersion(actualFeedVersion: FeedVersion, expectedFeedVersion: FeedVersion) {
            assertEquals(expectedFeedVersion.sha1, actualFeedVersion.sha1)
            assertEquals(expectedFeedVersion.feedOneStopId, actualFeedVersion.feedOneStopId)
            assertEquals(expectedFeedVersion.earliestCalendarDate, actualFeedVersion.earliestCalendarDate)
            assertEquals(expectedFeedVersion.latestCalendarDate, actualFeedVersion.latestCalendarDate)
            assertEquals(expectedFeedVersion.md5, actualFeedVersion.md5)
            assertEquals(expectedFeedVersion.fetchedAt, actualFeedVersion.fetchedAt)
            assertEquals(expectedFeedVersion.createdAt, actualFeedVersion.createdAt)
            assertEquals(expectedFeedVersion.importStatus, actualFeedVersion.importStatus)
            assertEquals(expectedFeedVersion.url, actualFeedVersion.url)
            assertEquals(expectedFeedVersion.downloadUrl, actualFeedVersion.downloadUrl)
            assertEquals(expectedFeedVersion.importLevel, actualFeedVersion.importLevel)
            assertEquals(expectedFeedVersion.isActiveFeedVersion, actualFeedVersion.isActiveFeedVersion)
            assertTrue(DateUtils.isToday(actualFeedVersion.updatedAt))
        }
    }
}
