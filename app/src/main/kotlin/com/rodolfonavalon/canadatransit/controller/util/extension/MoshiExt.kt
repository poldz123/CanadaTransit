package com.rodolfonavalon.canadatransit.controller.util.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

fun <T: Any> List<T>.toJson(): String
        = Moshi.Builder().build().adapter(List::class.java).nonNull().toJson(this)

fun <T: Any> String.fromJson(valueClazz: Class<T>): List<T> {
    val parameterizedType = Types.newParameterizedType(List::class.java, valueClazz)
    val adapter: JsonAdapter<List<T>> = Moshi.Builder().build().adapter(parameterizedType)
    return adapter.nonNull().fromJson(this)!!
}

fun DateTime.toJson(): String
        = ISODateTimeFormat.dateTime().print(this)

fun String.fromJson(): DateTime
        = ISODateTimeFormat.dateTimeParser().parseDateTime(this)