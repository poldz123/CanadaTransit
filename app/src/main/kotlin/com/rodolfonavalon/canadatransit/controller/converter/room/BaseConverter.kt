package com.rodolfonavalon.canadatransit.controller.converter.room

import androidx.room.TypeConverter
import com.rodolfonavalon.canadatransit.controller.util.extension.fromJsonDateTime
import com.rodolfonavalon.canadatransit.controller.util.extension.fromJsonList
import com.rodolfonavalon.canadatransit.controller.util.extension.toJson
import org.joda.time.DateTime

open class BaseConverter {

    @TypeConverter
    fun dateTimeToJson(value: DateTime): String =
            value.toJson()

    @TypeConverter
    fun jsonToDateTime(value: String): DateTime =
            value.fromJsonDateTime()

    @TypeConverter
    fun jsonToListInteger(value: String): List<Int> =
            value.fromJsonList()

    @TypeConverter
    fun listIntegerToJson(value: List<Int>): String =
            value.toJson()

    @TypeConverter
    fun jsonToListStrings(value: String): List<String> =
            value.fromJsonList()

    @TypeConverter
    fun listStringsToJson(value: List<String>): String =
            value.toJson()
}
