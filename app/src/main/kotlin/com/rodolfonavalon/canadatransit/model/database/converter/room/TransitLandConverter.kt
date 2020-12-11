package com.rodolfonavalon.canadatransit.model.database.converter.room

import android.arch.persistence.room.TypeConverter
import com.rodolfonavalon.canadatransit.controller.util.extension.fromJsonList
import com.rodolfonavalon.canadatransit.controller.util.extension.toJson
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorInFeed

class TransitLandConverter : BaseConverter() {

    @TypeConverter
    fun listOperatorInFeedToJson(value: List<OperatorInFeed>): String =
            value.toJson()

    @TypeConverter
    fun jsonToListOperatorInFeed(value: String): List<OperatorInFeed> =
            value.fromJsonList()
}
