package com.rodolfonavalon.canadatransit.controller.dao.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.user.UserOperators
import io.reactivex.Maybe

@Dao
interface UserOperatorsDao: BaseDao<UserOperators> {

    @Query("SELECT * FROM UserOperators WHERE operatorOneStopId = :operatorOneStopId")
    fun find(operatorOneStopId: String): Maybe<UserOperators>

    @Query("SELECT * FROM UserOperators")
    fun load(): Maybe<List<UserOperators>>

    @Query("DELETE FROM UserOperators")
    fun nuke(): Int

    @Query("SELECT * FROM Operator INNER JOIN UserOperators ON UserOperators.operatorOneStopId = Operator.operatorOneStopId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findOperators(): Maybe<List<Operator>>

    @Query("""
        SELECT * FROM OperatorFeed
        INNER JOIN Operator ON Operator.operatorOneStopId = OperatorFeed.operatorOneStopId
        INNER JOIN UserOperators ON UserOperators.operatorOneStopId = Operator.operatorOneStopId
        """)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    fun findOperatorFeeds(): Maybe<List<OperatorFeed>>
}
