package com.rodolfonavalon.canadatransit.model.database.converter.gson

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

    @FromJson fun fromJson(value: String): DateTime {
        val formatter = ISODateTimeFormat.dateTimeParser()
        return formatter.parseDateTime(value)
    }

    @ToJson fun toJson(dateTime: DateTime): String {
        val formatter = ISODateTimeFormat.dateTimeParser()
        return formatter.print(DateTime(dateTime))
    }
}
