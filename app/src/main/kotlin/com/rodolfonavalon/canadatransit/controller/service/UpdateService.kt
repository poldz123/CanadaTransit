package com.rodolfonavalon.canadatransit.controller.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class UpdateService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            return START_NOT_STICKY
        }


        return START_NOT_STICKY
    }
}

