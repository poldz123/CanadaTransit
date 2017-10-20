package com.rodolfonavalon.canadatransit.controller;

import android.app.Application;

import com.rodolfonavalon.canadatransit.BuildConfig;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class CanadaTransitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Joda-Time Android
        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: Release timber
        }
    }
}
