package com.rodolfonavalon.canadatransit.controller

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context

import com.rodolfonavalon.canadatransit.BuildConfig
import com.rodolfonavalon.canadatransit.model.database.dao.AppDatabase

import net.danlew.android.joda.JodaTimeAndroid

import timber.log.Timber

class CanadaTransitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Joda-Time Android
        JodaTimeAndroid.init(this)
        // Initialize the global application context
        appContext = applicationContext
        // Initialize the database
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() // TODO REMOVE
                .build()
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
    }
}
