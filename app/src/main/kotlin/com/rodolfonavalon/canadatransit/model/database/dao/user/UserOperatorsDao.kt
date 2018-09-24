package com.rodolfonavalon.canadatransit.model.database.dao.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rodolfonavalon.canadatransit.model.database.dao.BaseDao
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
}
