package com.rodolfonavalon.canadatransit.model.database.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rodolfonavalon.canadatransit.model.database.dao.transit.OperatorDao
import com.rodolfonavalon.canadatransit.model.database.dao.transit.OperatorFeedDao
import com.rodolfonavalon.canadatransit.model.database.dao.transit.OperatorFeedVersionDao
import com.rodolfonavalon.canadatransit.model.database.dao.user.UserOperatorsDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.user.UserOperators

@Database(entities = [
            Operator::class,
            OperatorFeed::class,
            OperatorFeedVersion::class,
            UserOperators::class
        ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun operatorDao(): OperatorDao
    abstract fun operatorFeedDao(): OperatorFeedDao
    abstract fun operatorFeedVersionDao(): OperatorFeedVersionDao
    abstract fun userOperatorsDao(): UserOperatorsDao
}
