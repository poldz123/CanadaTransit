package com.rodolfonavalon.canadatransit.model.database.dao.transit

import android.arch.persistence.room.*
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import io.reactivex.Single

@Dao
interface TransitLandDao {

    @Query("SELECT * FROM Operator WHERE oneStopId = :oneStopId")
    fun findOperator(oneStopId: String): Single<Operator>

    @Query("SELECT * FROM OperatorFeed WHERE feedOneStopId = :feedOneStopId")
    fun findOperatorFeed(feedOneStopId: String): Single<OperatorFeed>

    @Query("SELECT * FROM OperatorFeedVersion WHERE feedOneStopId = :feedOneStopId")
    fun findOperatorFeedVersion(feedOneStopId: String): Single<OperatorFeedVersion>

    @Query("SELECT * FROM Operator")
    fun loadOperators(): Single<List<Operator>>

    @Query("SELECT * FROM OperatorFeed")
    fun loadOperatorFeeds(): Single<List<OperatorFeed>>

    @Query("SELECT * FROM OperatorFeedVersion")
    fun loadOperatorFeedVersions(): Single<List<OperatorFeedVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperators(vararg operators: Operator): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperatorFeeds(vararg operatorFeeds: OperatorFeed): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperatorFeedVersions(vararg operatorFeedVersions: OperatorFeedVersion): List<Long>

    @Query("DELETE FROM Operator")
    fun nukeOperator(): Int

    @Query("DELETE FROM OperatorFeed")
    fun nukeOperatorFeed(): Int

    @Query("DELETE FROM OperatorFeedVersion")
    fun nukeOperatorFeedVersion(): Int

    @Delete
    fun deleteOperator(vararg operators: Operator): Int

    @Delete
    fun deleteOperatorFeed(vararg operatorFeeds: OperatorFeed): Int

    @Delete
    fun deleteOperatorFeedVersion(vararg operatorFeedVersions: OperatorFeedVersion): Int
}