package com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter

import com.rodolfonavalon.canadatransit.controller.util.extension.fromJsonDateTime
import com.rodolfonavalon.canadatransit.controller.util.extension.toJson
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.joda.time.DateTime

/**
 * This is the [Moshi] converter for the [DateTime] that serialize and deserialize from the
 * Room database.
 */
class DateTimeAdapter {

    @FromJson fun fromJson(value: String?): DateTime? {
        // Returns null when the json value for date is null,
        // this could mean that the server spits out wrong data.
        return value?.fromJsonDateTime()
    }

    @ToJson fun toJson(dateTime: DateTime): String {
        return dateTime.toJson()
    }
}
