package com.rodolfonavalon.canadatransit.controller.database.dao.user

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.database.converter.room.BaseConverter
import com.rodolfonavalon.canadatransit.controller.database.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import io.reactivex.Maybe
import org.joda.time.DateTime

@Dao
interface UserTransitDao : BaseDao<UserTransit> {

    @Query("SELECT * FROM UserTransit WHERE operatorOneStopId = :operatorOneStopId")
    fun find(operatorOneStopId: String): Maybe<UserTransit>

    @Query("SELECT * FROM UserTransit")
    fun load(): Maybe<List<UserTransit>>

    @Query("DELETE FROM UserTransit")
    fun nuke(): Int

    @Query("UPDATE UserTransit SET updatedAt = :updatedAt")
    @TypeConverters(BaseConverter::class)
    fun updateAll(updatedAt: DateTime): Int

    @Query("SELECT * FROM Operator INNER JOIN UserTransit ON UserTransit.operatorOneStopId = Operator.operatorOneStopId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findOperators(): Maybe<List<Operator>>

    @Query("""
        SELECT * FROM Feed
        INNER JOIN Operator ON Operator.operatorOneStopId = Feed.operatorOneStopId
        INNER JOIN UserTransit ON UserTransit.operatorOneStopId = Operator.operatorOneStopId
        """)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findFeeds(): Maybe<List<Feed>>

    @Query("""
        SELECT * FROM FeedVersion
        INNER JOIN Feed ON Feed.feedOneStopId = FeedVersion.feedOneStopId
        INNER JOIN Operator ON Operator.operatorOneStopId = Feed.operatorOneStopId
        INNER JOIN UserTransit ON UserTransit.operatorOneStopId = Operator.operatorOneStopId
        """)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findFeedVersions(): Maybe<List<FeedVersion>>
}
