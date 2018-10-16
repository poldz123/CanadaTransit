package com.rodolfonavalon.canadatransit.controller.database.converter.moshi.adapter

import com.rodolfonavalon.canadatransit.controller.util.DebugUtil
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorInFeed
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class FeedForeignKey

class FeedForeignKeyAdapter {
    @FromJson
    @FeedForeignKey
    fun fromJson(operatorsInFeed: List<OperatorInFeed>): String {
        DebugUtil.assertTrue(operatorsInFeed.isNotEmpty(), "There are no operator found, make sure to skip them.")
        return operatorsInFeed.first().operatorOneStopId
    }

    @ToJson
    fun toJson(@FeedForeignKey value: String): String? {
        // We return null here since this value does not exist in the API response.
        return null
    }
}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class FeedCurrentFeedVersion

class FeedCurrentFeedVersionAdapter {
    @FromJson
    @FeedCurrentFeedVersion
    fun fromJson(operatorsInFeed: List<String>): String {
        if (operatorsInFeed.isEmpty()) {
            return ""
        }
        return operatorsInFeed.last()
    }

    @ToJson
    fun toJson(@FeedCurrentFeedVersion value: String): String? {
        // We return null here since this value does not exist in the API response.
        return null
    }
}