package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.rodolfonavalon.canadatransit.controller.util.extension.fromJson
import com.rodolfonavalon.canadatransit.controller.util.extension.toJson
import org.joda.time.DateTime

open class BaseConverter {

    @TypeConverter
    fun dateTimeToJson(value: DateTime): String
            = value.toJson()

    @TypeConverter
    fun jsonToDateTime(value: String): DateTime
            = value.fromJson()

    @TypeConverter
    fun jsonToListInteger(value: String): List<Int>
            = value.fromJson(Int::class.java)

    @TypeConverter
    fun listIntegerToJson(value: List<Int>): String
            = value.toJson()

    @TypeConverter
    fun jsonToListStrings(value: String): List<String>
            = value.fromJson(String::class.java)

    @TypeConverter
    fun listStringsToJson(value: List<String>): String
            = value.toJson()
}
