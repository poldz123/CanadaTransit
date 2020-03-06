package com.rodolfonavalon.canadatransit.controller.database.dao.transit

import androidx.room.Dao
import androidx.room.Query
import com.rodolfonavalon.canadatransit.controller.database.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import io.reactivex.Maybe

@Dao
interface FeedVersionDao : BaseDao<FeedVersion> {

    @Query("SELECT * FROM FeedVersion WHERE feedOneStopId = :feedOneStopId")
    fun find(feedOneStopId: String): Maybe<FeedVersion>

    @Query("SELECT * FROM FeedVersion")
    fun load(): Maybe<List<FeedVersion>>

    @Query("DELETE FROM FeedVersion")
    fun nuke(): Maybe<Int>
}
