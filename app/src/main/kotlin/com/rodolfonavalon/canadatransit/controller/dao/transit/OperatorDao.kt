package com.rodolfonavalon.canadatransit.controller.dao.transit

import androidx.room.Dao
import androidx.room.Query
import com.rodolfonavalon.canadatransit.controller.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.Maybe

@Dao
interface OperatorDao : BaseDao<Operator> {

    @Query("SELECT * FROM Operator WHERE operatorOneStopId = :operatorOneStopId")
    fun find(operatorOneStopId: String): Maybe<Operator>

    @Query("SELECT * FROM Operator")
    fun load(): Maybe<List<Operator>>

    @Query("DELETE FROM Operator")
    fun nuke(): Int

    @Query("SELECT * FROM Operator INNER JOIN UserOperators ON UserOperators.operatorOneStopId = Operator.operatorOneStopId")
    fun findOperatorsOfUser(): Maybe<List<Operator>>
}
