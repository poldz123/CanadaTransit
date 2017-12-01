package com.rodolfonavalon.canadatransit.model.database.converter.gson

import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type

/**
 * This is the [Gson] converter for the [DateTime] that serialize and deserialize from the
 * Room database.
 */
class DateTimeConverter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime? {
        if (json.asString == null || json.asString.isEmpty()) {
            return null
        }
        val formatter = ISODateTimeFormat.dateTimeParser()
        return formatter.parseDateTime(json.asString)
    }

    override fun serialize(src: DateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val formatter = ISODateTimeFormat.dateTimeParser()
        return JsonPrimitive(formatter.print(DateTime(src)))
    }
}
