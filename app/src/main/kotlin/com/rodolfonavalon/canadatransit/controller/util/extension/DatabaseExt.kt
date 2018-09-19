package com.rodolfonavalon.canadatransit.controller.util.extension

import com.rodolfonavalon.canadatransit.model.database.dao.BaseDao
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Query the database using the Reactive pattern for [Maybe]. This method can only be
 * used for queries that returns an object.
 *
 * @param block the block runnable that should emit the result from the query
 * @return The [Maybe] observable with the result of the query
 */
inline fun <DAO: BaseDao<MODEL>, MODEL: Any, RESULT> DAO.dbQuery(crossinline block: DAO.() -> RESULT): Maybe<RESULT> {
    return Maybe.fromCallable { block(this) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Deletes an object within the database using the Reactive pattern. This method can only
 * be used for deleting objects within the database.
 *
 * @param block the block runnable that should emit the result from the delete
 * @return The [Maybe] observable with the result of the delete
 */
inline fun <DAO: BaseDao<MODEL>, MODEL: Any, RESULT> DAO.dbDelete(crossinline block: DAO.() -> RESULT): Maybe<RESULT> {
    return Maybe.fromCallable { block(this) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Inserts an object within the database using the Reactive pattern. This method can only
 * be used for inserting objects within the database.
 *
 * @param block the block runnable that should emit the result from the insert
 * @return The [Maybe] observable with the result of the insert
 */
inline fun <DAO: BaseDao<MODEL>, MODEL: Any, RESULT> DAO.dbInsert(crossinline block: DAO.() -> RESULT): Maybe<RESULT> {
    return Maybe.fromCallable { block(this) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Updates an object within the database using the Reactive pattern. This method can only
 * be used for updating objects within the database.
 *
 * @param block the block runnable that should emit the result from the update
 * @return The [Maybe] observable with the result of the updates
 */
inline fun <DAO: BaseDao<MODEL>, MODEL: Any, RESULT> DAO.dbUpdate(crossinline block: DAO.() -> RESULT): Maybe<RESULT> {
    return Maybe.fromCallable { block(this) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

