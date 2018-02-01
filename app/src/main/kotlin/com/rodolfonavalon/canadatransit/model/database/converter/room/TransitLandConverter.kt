package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorInFeed

class TransitLandConverter: BaseConverter() {

    @TypeConverter
    fun listOperatorInFeedToString(value: List<OperatorInFeed>): String
            = Gson().toJson(value)

    @TypeConverter
    fun stringToListOperatorInFeed(value: String): List<OperatorInFeed>
            = Gson().fromJson(value, Array<OperatorInFeed>::class.java).toList()
}