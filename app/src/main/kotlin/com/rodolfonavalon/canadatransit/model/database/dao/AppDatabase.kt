package com.rodolfonavalon.canadatransit.model.database.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.dao.transit.TransitLandDao

@Database(entities = [
        Operator::class,
        OperatorFeed::class,
        OperatorFeedVersion::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transitLandDao(): TransitLandDao
}