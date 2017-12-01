package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

open class BaseConverter {

    @TypeConverter
    fun dateTimeToString(value: DateTime): String
            = ISODateTimeFormat.dateTimeParser().print(value)

    @TypeConverter
    fun stringToDateTime(value: String): DateTime
            = ISODateTimeFormat.dateTimeParser().parseDateTime(value)

    @TypeConverter
    fun listIntegerToString(value: List<Int>): String
            = Gson().toJson(value)

    @TypeConverter
    fun stringToListInteger(value: String): List<Int>
            = Gson().fromJson(value, Array<Int>::class.java).toList()

    @TypeConverter
    fun stringToListStrings(value: String): List<String>
            = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun listStringsToString(association: List<String>): String
            = Gson().toJson(association)
}
