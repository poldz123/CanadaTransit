package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.rodolfonavalon.canadatransit.controller.util.MoshiUtil
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorInFeed

class TransitLandConverter: BaseConverter() {

    @TypeConverter
    fun listOperatorInFeedToString(value: List<OperatorInFeed>): String
            = MoshiUtil.toJson(value)

    @TypeConverter
    fun stringToListOperatorInFeed(value: String): List<OperatorInFeed>
            = MoshiUtil.fromJson(value, OperatorInFeed::class.java)
}