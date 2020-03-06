package com.rodolfonavalon.canadatransit.controller.database.dao.transit

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.rodolfonavalon.canadatransit.controller.database.dao.BaseDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import io.reactivex.Maybe

@Dao
interface OperatorDao : BaseDao<Operator> {

    @Query("SELECT * FROM Operator WHERE operatorOneStopId = :operatorOneStopId")
    fun find(operatorOneStopId: String): Maybe<Operator>

    @Query("SELECT * FROM Operator")
    fun load(): Maybe<List<Operator>>

    @Query("SELECT * FROM Operator ORDER BY Operator.name")
    fun loadLive(): LiveData<List<Operator>>

    @Query("DELETE FROM Operator")
    fun nuke(): Maybe<Int>
}
