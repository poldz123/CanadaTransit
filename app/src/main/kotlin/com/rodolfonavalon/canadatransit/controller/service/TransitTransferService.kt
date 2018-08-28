package com.rodolfonavalon.canadatransit.controller.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion

class TransitTransferService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            return START_NOT_STICKY
        }

        return START_NOT_STICKY
    }

    companion object {
        private const val ACTION_TYPE_KEY: String = "transit_service_action_type"
        private enum class ACTION_TYPE {
            DOWNLOAD
        }

        private const val DOWNLOAD_LINK_KEY: String = "transit_service_download_link"
        fun download(operatorFeedVersion: OperatorFeedVersion) {

        }
    }
}