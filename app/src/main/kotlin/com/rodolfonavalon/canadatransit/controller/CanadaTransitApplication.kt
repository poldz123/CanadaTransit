package com.rodolfonavalon.canadatransit.controller

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context

import com.rodolfonavalon.canadatransit.BuildConfig
import com.rodolfonavalon.canadatransit.model.database.dao.AppDatabase

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
        appDatabase = when (isEnabledDatabaseOnMainThread) {
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
        private var isEnabledDatabaseOnMainThread = false

        fun enableDatabaseOnMainThread() {
            isEnabledDatabaseOnMainThread = true
        }

        fun disableDatabaseOnMainThread() {
            isEnabledDatabaseOnMainThread = false
        }
    }
}
