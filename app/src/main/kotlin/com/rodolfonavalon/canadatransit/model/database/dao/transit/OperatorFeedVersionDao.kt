package com.rodolfonavalon.canadatransit.model.database.dao.transit

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rodolfonavalon.canadatransit.model.database.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.Maybe

@Dao
interface OperatorFeedVersionDao : BaseDao<OperatorFeedVersion> {

    @Query("SELECT * FROM OperatorFeedVersion WHERE feedOneStopId = :feedOneStopId")
    fun find(feedOneStopId: String): Maybe<OperatorFeedVersion>

    @Query("SELECT * FROM OperatorFeedVersion")
    fun load(): Maybe<List<OperatorFeedVersion>>

    @Query("DELETE FROM OperatorFeedVersion")
    fun nuke(): Int
}
