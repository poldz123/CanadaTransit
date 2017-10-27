package com.rodolfonavalon.canadatransit.model.database.converter.gson

import android.text.TextUtils
import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type
import java.util.*

/**
 * This is the [Gson] converter for the [Date] that serialize and deserialize from the
 * Realm database.
 */
class DateConverter : JsonSerializer<Date>, JsonDeserializer<Date> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        if (TextUtils.isEmpty(json.asString)) {
            return null
        }
        val formatter = ISODateTimeFormat.dateTimeParser()
        return formatter.parseDateTime(json.asString).toDate()
    }

    override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val formatter = ISODateTimeFormat.dateTimeParser()
        return JsonPrimitive(formatter.print(DateTime(src)))
    }
}
