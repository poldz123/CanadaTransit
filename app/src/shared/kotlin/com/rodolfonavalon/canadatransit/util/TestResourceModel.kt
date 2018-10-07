package com.rodolfonavalon.canadatransit.util

import com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter.DateTimeAdapter
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.database.transit.Tags

object TestResourceModel {

    object OperatorModel {

        fun createOCTranspoModel(): Operator {
            return Operator(
                    "o-f24-octranspo",
                    "OC Transpo",
                    "CA-ON",
                    "America/Montreal",
                    DateTimeAdapter().fromJson("2016-05-26T21:32:08.877Z")!!,
                    DateTimeAdapter().fromJson("2017-11-07T00:55:35.171Z")!!,
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
                    DateTimeAdapter().fromJson("2017-10-23T04:57:10.108Z")!!,
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
    }

    object FeedModel {

        fun createOCTranspoModel(): Feed {
            return Feed(
                    "f-f24-octranspo",
                    "o-f24-octranspo",
                    null,
                    DateTimeAdapter().fromJson("2016-05-26T21:32:08.883Z")!!,
                    DateTimeAdapter().fromJson("2018-08-28T22:30:12.841Z")!!,
                    "http://www.octranspo1.com/files/google_transit.zip",
                    "gtfs",
                    DateTimeAdapter().fromJson("2018-08-28T22:30:12.837Z")!!,
                    DateTimeAdapter().fromJson("2018-08-05T11:56:56.400Z")!!,
                    "most_recent_succeeded",
                    "03ee676c71cdce519b0723f1a455bb838dc9ecf6",
                    "https://api.transit.land/api/v1/feed_versions?feed_onestop_id=f-f24-octranspo"
            )
        }

        fun createAMTTranspoModel(): Feed {
            return Feed(
                    "f-f25d-agencemtropolitainedetransportexpress",
                    "o-f25-agencemtropolitainedetransport",
                    null,
                    DateTimeAdapter().fromJson("2016-08-26T23:08:40.138Z")!!,
                    DateTimeAdapter().fromJson("2018-08-29T02:00:24.556Z")!!,
                    "http://www.amt.qc.ca/xdata/express/google_transit.zip",
                    "gtfs",
                    DateTimeAdapter().fromJson("2018-08-29T02:00:24.553Z")!!,
                    DateTimeAdapter().fromJson("2017-06-24T15:12:28.891Z")!!,
                    "most_recent_succeeded",
                    "1b99f0448fb3ba210ea1b669529d60eeb5699a9b",
                    "https://api.transit.land/api/v1/feed_versions?feed_onestop_id=f-f25d-agencemtropolitainedetransportexpress"
            )
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
                    DateTimeAdapter().fromJson("2017-11-07T02:15:22.661Z")!!,
                    DateTimeAdapter().fromJson("2017-10-27T21:30:17.354Z")!!,
                    DateTimeAdapter().fromJson("2017-11-07T02:15:22.733Z")!!,
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
                    DateTimeAdapter().fromJson("2017-06-24T15:12:28.891Z")!!,
                    DateTimeAdapter().fromJson("2017-06-22T17:30:28.098Z")!!,
                    DateTimeAdapter().fromJson("2017-06-25T14:11:47.797Z")!!,
                    "most_recent_succeeded",
                    "http://www.amt.qc.ca/xdata/express/google_transit.zip",
                    "https://transitland-gtfs.s3.amazonaws.com/datastore-uploads/feed_version/1b99f0448fb3ba210ea1b669529d60eeb5699a9b.zip",
                    4,
                    true
            )
        }
    }
}