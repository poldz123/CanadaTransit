package com.rodolfonavalon.canadatransit.controller;

import android.app.Application;

import com.rodolfonavalon.canadatransit.BuildConfig;

import timber.log.Timber;

public class CanadaTransitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: Release timber
        }
    }
}
