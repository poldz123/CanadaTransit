package com.rodolfonavalon.canadatransit.controller.dao.transit

import androidx.room.Dao
import androidx.room.Query
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import io.reactivex.Maybe

@Dao
interface FeedDao : BaseDao<Feed> {

    @Query("SELECT * FROM Feed WHERE feedOneStopId = :feedOneStopId")
    fun find(feedOneStopId: String): Maybe<Feed>

    @Query("SELECT * FROM Feed WHERE feedOneStopId IN (:feedOneStopIds)")
    fun find(feedOneStopIds: List<String>): Maybe<List<Feed>>

    @Query("SELECT * FROM Feed")
    fun load(): Maybe<List<Feed>>

    @Query("DELETE FROM Feed")
    fun nuke(): Int
}
