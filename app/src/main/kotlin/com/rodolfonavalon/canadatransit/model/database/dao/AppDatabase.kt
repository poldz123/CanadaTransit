package com.rodolfonavalon.canadatransit.model.database.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.dao.transit.TransitLandDao
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Database(entities = arrayOf(
        Operator::class,
        OperatorFeed::class,
        OperatorFeedVersion::class
), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transitLandDao(): TransitLandDao

    companion object {

        /**
         * Query the database using the Reactive pattern for [Single]. This method can only be
         * used for queries that returns an object.
         *
         * @param source the database source reactive single value response
         * @param success the success callback with the return value of the query
         * @param error the error callback when something went wrong during the query
         */
        fun <R> query(source: Single<R>, success: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        /**
         * Query the database using the Reactive pattern for [Maybe]. This method can only be
         * used for queries that returns an object.
         *
         * @param source the database source reactive single value response
         * @param success the success callback with the return value of the query
         * @param error the error callback when something went wrong during the query
         */
        fun <R> query(source: Maybe<R>, success: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        /**
         * Query the database using the Reactive pattern for [Flowable]. This method can only be
         * used for queries that returns an object.
         *
         * @param source the database source reactive single value response
         * @param update the update callback with the return value of the query
         * @param error the error callback when something went wrong during the query
         */
        fun <R> query(source: Flowable<R>, update: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(update, error)
        }

        /**
         * Deletes an object within the database using the Reactive pattern. This method can only
         * be used for deleting objects within the database.
         *
         * @param delete the callback method for deleting objects
         * @param success the success callback that receives the number of rows successfully deleted
         * @param error the error callback when something went wrong during the deletion
         */
        fun delete(delete: () -> Int, success: (Int) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(delete)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        /**
         * Inserts an object within the database using the Reactive pattern. This method can only
         * be used for inserting objects within the database.
         *
         * @param insert the callback method for inserting objects
         * @param success the success callback that receives the number of row ids successfully inserted
         * @param error the error callback when something went wrong during the insertion
         */
        fun insert(insert: () -> List<Long>, success: (List<Long>) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(insert)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)

        }

        /**
         * Updates an object within the database using the Reactive pattern. This method can only
         * be used for updating objects within the database.
         *
         * @param update the callback method for updating objects
         * @param success the success callback that receives the number of row successfully updated
         * @param error the error callback when something went wrong during the update
         */
        fun update(update: () -> Int, success: (Int) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(update)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }
    }
}