package com.rodolfonavalon.canadatransit.controller.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.model.database.dao.transit.TransitLandDao
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed

class UpdateService: Service() {

    private enum class UpdateCommand {
        OPERATOR,
        OPERATOR_FEEDS,
        OPERATOR_FEEDS_VERSION,
        EVERYTHING
    }

    private val transitLandDao: TransitLandDao by lazy {
        CanadaTransitApplication.appDatabase.transitLandDao()
    }

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

        fun updateOperators() {
            // Todo - This updates the operator, we do not care if it have a flag
            // that needs to be updated. All operators are updated by default every time.
        }

        fun updateOperatorFeeds() {
            // Todo - This updates only when it has a flag that consider it to be updated.
            // the flag is added when user selected a particular operator.
        }

        fun updateOperatorFeed(operator: Operator) {
            // Todo - This adds the operator feeds to the pool of operator feeds that needs to be updated.
            // and updates it within the database
        }

        fun updateOperatorFeedVersions() {
            // Todo - This updates same as the operator feeds if the user has selected a particular
            // operator and operator feed.
        }

        fun updateOperatorFeedVersion(operatorFeed: OperatorFeed) {
            // Todo - This adds the operator feeds version to the pool of operator feeds that needs to be updated.
            // and updates it within the database
        }

        fun updateEverything() {
            // Todo - This updates everything, from operator, operator feeds, operator feed version.
        }
    }
}

