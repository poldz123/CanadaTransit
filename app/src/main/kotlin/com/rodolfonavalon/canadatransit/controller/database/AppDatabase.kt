package com.rodolfonavalon.canadatransit.controller.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.FeedVersionDao
import com.rodolfonavalon.canadatransit.controller.database.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.controller.database.dao.user.UserTransitDao
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit

@Database(entities = [
            Operator::class,
            Feed::class,
            FeedVersion::class,
            UserTransit::class
        ], version = 1) // TODO(change back to 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun operatorDao(): OperatorDao
    abstract fun feedDao(): FeedDao
    abstract fun feedVersionDao(): FeedVersionDao
    abstract fun userTransitDao(): UserTransitDao
}
