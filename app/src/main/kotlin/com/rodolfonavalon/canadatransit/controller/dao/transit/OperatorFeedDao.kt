package com.rodolfonavalon.canadatransit.controller.dao.transit

import androidx.room.Dao
import androidx.room.Query
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import io.reactivex.Maybe

@Dao
interface OperatorFeedDao : BaseDao<OperatorFeed> {

    @Query("SELECT * FROM OperatorFeed WHERE feedOneStopId = :feedOneStopId")
    fun find(feedOneStopId: String): Maybe<OperatorFeed>

    @Query("SELECT * FROM OperatorFeed WHERE feedOneStopId IN (:feedOneStopIds)")
    fun find(feedOneStopIds: List<String>): Maybe<List<OperatorFeed>>

    @Query("SELECT * FROM OperatorFeed")
    fun load(): Maybe<List<OperatorFeed>>

    @Query("DELETE FROM OperatorFeed")
    fun nuke(): Int
}
