package com.rodolfonavalon.canadatransit.model.database.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
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

        fun <R> query(source: Single<R>, success: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        fun <R> query(source: Maybe<R>, success: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        fun <R> query(source: Flowable<R>, update: (R) -> Unit, error: (Throwable) -> Unit) {
            source.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(update, error)
        }

        fun delete(delete: () -> Int, success: (Int) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(delete)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }

        fun insert(insert: () -> List<Long>, success: (List<Long>) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(insert)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)

        }

        fun update(update: () -> Int, success: (Int) -> Unit, error: (Throwable) -> Unit) {
            Maybe.fromCallable(update)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
        }
    }
}