package com.rodolfonavalon.canadatransit.controller.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodolfonavalon.canadatransit.controller.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.controller.dao.transit.OperatorFeedDao
import com.rodolfonavalon.canadatransit.controller.dao.transit.OperatorFeedVersionDao
import com.rodolfonavalon.canadatransit.controller.dao.user.UserTransitDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit

@Database(entities = [
            Operator::class,
            OperatorFeed::class,
            OperatorFeedVersion::class,
            UserTransit::class
        ], version = 4) // TODO(change back to 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun operatorDao(): OperatorDao
    abstract fun operatorFeedDao(): OperatorFeedDao
    abstract fun operatorFeedVersionDao(): OperatorFeedVersionDao
    abstract fun userTransitDao(): UserTransitDao
}
