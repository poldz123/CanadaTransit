package com.rodolfonavalon.canadatransit

import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.util.DebugUtil

class JvmCanadaTransitApplication: CanadaTransitApplication() {

    override fun onCreate() {
        // The database should be allowed to run in the main thread. Make sure that this is
        // called before the main application on create is triggered, since this flag
        // is called within the application's onCreate
        enableDatabaseOnMainThread()
        super.onCreate()
        // Needs to disable the checks since the unit test will run in the main thread.
        DebugUtil.disableBackgroundChecks()
    }
}