package com.rodolfonavalon.canadatransit.controller.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueTaskListener
import timber.log.Timber

class UpdateService: Service(), QueueTaskListener {

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
            UpdateManager.manager().listener = this
            UpdateManager.manager().start()
        }

        return START_NOT_STICKY
    }

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onStart() {
        Timber.d("Update Manager is starting...")
    }

    override fun onFinish() {
        Timber.d("Update Manager is finished...")
    }

    companion object {

        // This an an action within the service that will trigger the update manager
        // to start its queued tasks.
        const val ACTION_START_UPDATE_MANAGER = "ACTION_START_UPDATE_MANAGER"
    }
}

