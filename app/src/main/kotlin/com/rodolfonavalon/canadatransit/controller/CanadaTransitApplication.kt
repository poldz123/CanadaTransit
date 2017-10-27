package com.rodolfonavalon.canadatransit.controller

import android.app.Application

import com.rodolfonavalon.canadatransit.BuildConfig

import net.danlew.android.joda.JodaTimeAndroid

import timber.log.Timber

class CanadaTransitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Joda-Time Android
        JodaTimeAndroid.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // TODO: Release timber
        }
    }
}
