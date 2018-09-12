package com.rodolfonavalon.canadatransit.model.database.converter.moshi.adapter

import com.squareup.moshi.FromJson
import org.joda.time.DateTime
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.joda.time.format.ISODateTimeFormat

/**
 * This is the [Moshi] converter for the [DateTime] that serialize and deserialize from the
 * Room database.
 */
class DateTimeAdapter {

    @FromJson fun fromJson(value: String?): DateTime? {
        // Returns null when the json value for date is null or empty,
        // this could mean that the server spits out wrong data.
        if (value.isNullOrEmpty()) {
            return null
        }
        val formatter = ISODateTimeFormat.dateTimeParser()
        return formatter.parseDateTime(value)
    }

    @ToJson fun toJson(dateTime: DateTime): String {
        val formatter = ISODateTimeFormat.dateTime()
        return formatter.print(DateTime(dateTime))
    }
}
