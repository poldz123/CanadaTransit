package com.rodolfonavalon.canadatransit.model.database.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.dao.transit.TransitLandDao

@Database(entities = arrayOf(
        Operator::class,
        OperatorFeed::class,
        OperatorFeedVersion::class
), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transitLandDao(): TransitLandDao
}