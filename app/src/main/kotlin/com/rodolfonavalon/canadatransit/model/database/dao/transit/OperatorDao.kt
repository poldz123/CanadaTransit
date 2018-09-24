package com.rodolfonavalon.canadatransit.model.database.dao.transit

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rodolfonavalon.canadatransit.model.database.dao.BaseDao
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
