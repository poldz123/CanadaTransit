package com.rodolfonavalon.canadatransit.controller.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import timber.log.Timber

class UpdateService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }

        // Start the update manager within the service, this is to be able to
        // update in the background even if the application has already been destroyed.
        val isStartUpdateManager = intent.getBooleanExtra(ACTION_START_UPDATE_MANAGER, false)
        if (isStartUpdateManager) {
            UpdateManager.startManager(::onUpdateManagerComplete)
        }

        return START_NOT_STICKY
    }

    private fun onUpdateManagerComplete() {
        Timber.d("Update Manager is complete...")
    }

    companion object {

        const val ACTION_START_UPDATE_MANAGER = "ACTION_START_UPDATE_MANAGER"
    }
}

