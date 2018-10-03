package com.rodolfonavalon.canadatransit.controller.dao.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.user.UserTransit
import io.reactivex.Maybe

@Dao
interface UserTransitDao: BaseDao<UserTransit> {

    @Query("SELECT * FROM UserTransit WHERE operatorOneStopId = :operatorOneStopId")
    fun find(operatorOneStopId: String): Maybe<UserTransit>

    @Query("SELECT * FROM UserTransit")
    fun load(): Maybe<List<UserTransit>>

    @Query("DELETE FROM UserTransit")
    fun nuke(): Int

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
}
