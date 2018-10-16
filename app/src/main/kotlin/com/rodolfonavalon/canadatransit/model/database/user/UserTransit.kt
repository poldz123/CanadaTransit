package com.rodolfonavalon.canadatransit.model.database.user

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.database.converter.room.BaseConverter
import org.joda.time.DateTime

@Entity
@TypeConverters(BaseConverter::class)
data class UserTransit(
        @PrimaryKey
        val operatorOneStopId: String,
        val updatedAt: DateTime // TODO put to null?
)
