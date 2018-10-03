package com.rodolfonavalon.canadatransit.controller.dao.user

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.converter.room.BaseConverter
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import io.reactivex.Maybe
import org.joda.time.DateTime

@Dao
interface UserTransitDao: BaseDao<UserTransit> {

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
        SELECT * FROM OperatorFeed
        INNER JOIN Operator ON Operator.operatorOneStopId = OperatorFeed.operatorOneStopId
        INNER JOIN UserTransit ON UserTransit.operatorOneStopId = Operator.operatorOneStopId
        """)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findOperatorFeeds(): Maybe<List<OperatorFeed>>

    @Query("""
        SELECT * FROM OperatorFeedVersion
        INNER JOIN OperatorFeed ON OperatorFeed.feedOneStopId = OperatorFeedVersion.feedOneStopId
        INNER JOIN Operator ON Operator.operatorOneStopId = OperatorFeed.operatorOneStopId
        INNER JOIN UserTransit ON UserTransit.operatorOneStopId = Operator.operatorOneStopId
        """)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findOperatorFeedVersions(): Maybe<List<OperatorFeedVersion>>
}
