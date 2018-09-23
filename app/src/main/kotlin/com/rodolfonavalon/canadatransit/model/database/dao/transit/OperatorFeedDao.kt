package com.rodolfonavalon.canadatransit.model.database.dao.transit

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rodolfonavalon.canadatransit.model.database.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import io.reactivex.Maybe

@Dao
interface OperatorFeedDao : BaseDao<OperatorFeed> {

    @Query("SELECT * FROM OperatorFeed WHERE feedOneStopId = :feedOneStopId")
    fun find(feedOneStopId: String): Maybe<OperatorFeed>

    @Query("SELECT * FROM OperatorFeed")
    fun load(): Maybe<List<OperatorFeed>>

    @Query("DELETE FROM OperatorFeed")
    fun nuke(): Int
}
