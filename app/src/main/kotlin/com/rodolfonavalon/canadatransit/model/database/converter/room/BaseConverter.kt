package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.rodolfonavalon.canadatransit.controller.util.MoshiUtil
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

open class BaseConverter {

    @TypeConverter
    fun dateTimeToString(value: DateTime): String
            = ISODateTimeFormat.dateTime().print(value)

    @TypeConverter
    fun stringToDateTime(value: String): DateTime
            = ISODateTimeFormat.dateTimeParser().parseDateTime(value)

    @TypeConverter
    fun listIntegerToString(value: List<Int>): String
            = MoshiUtil.toJson(value)

    @TypeConverter
    fun stringToListInteger(value: String): List<Int>
            = MoshiUtil.fromJson(value, Int::class.java)

    @TypeConverter
    fun stringToListStrings(value: String): List<String>
            = MoshiUtil.fromJson(value, String::class.java)

    @TypeConverter
    fun listStringsToString(value: List<String>): String
            = MoshiUtil.toJson(value)
}
