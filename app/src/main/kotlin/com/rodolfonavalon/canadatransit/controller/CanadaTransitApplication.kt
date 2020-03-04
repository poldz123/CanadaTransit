package com.rodolfonavalon.canadatransit.controller

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.rodolfonavalon.canadatransit.BuildConfig
import com.rodolfonavalon.canadatransit.controller.database.AppDatabase
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

open class CanadaTransitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Joda-Time Android
        JodaTimeAndroid.init(this)
        // Initialize the global application context
        appContext = applicationContext
        // Initialize the database
        appDatabase = when (isEnableJVMTest) {
            true ->
                Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build()
            false ->
                Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration() // TODO REMOVE
                    .build()
        }
        // Initialize the Timber for both DEBUG and RELEASE Tree
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO Release timber
        }
    }

    companion object {
        const val DATABASE_NAME = "canada_transit_database"

        lateinit var appContext: Context
            private set
        lateinit var appDatabase: AppDatabase
            private set

        // This enables support the database to be queried in the main thread instead
        // in the background thread which is by default.
        private var isEnableJVMTest = false

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun enableDatabaseOnMainThread() {
            isEnableJVMTest = true
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun disableDatabaseOnMainThread() {
            isEnableJVMTest = false
        }
    }
}
