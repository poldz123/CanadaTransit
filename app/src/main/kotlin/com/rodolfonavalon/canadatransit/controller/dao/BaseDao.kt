package com.rodolfonavalon.canadatransit.controller.dao

import androidx.room.*

@Dao
interface BaseDao<MODEL> {

    /**
     * Insert an object in the database.
     *
     * @param models the list of models to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(models: List<MODEL>): List<Long>

    /**
     * Insert an array of objects in the database.
     *
     * @param models the arguments of models to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg models: MODEL): List<Long>

    /**
     * Update an object from the database.
     *
     * @param model the model to be updated
     */
    @Update
    fun update(model: MODEL): Int

    /**
     * Delete an object from the database
     *
     * @param model the model to be deleted
     */
    @Delete
    fun delete(model: MODEL): Int
}
