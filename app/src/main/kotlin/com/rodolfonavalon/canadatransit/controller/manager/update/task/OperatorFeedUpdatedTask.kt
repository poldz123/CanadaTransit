package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.util.AbstractUpdateTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator

class OperatorFeedUpdatedTask(updateManager: UpdateManager, trackingId: String):
        AbstractUpdateTask(updateManager, trackingId) {

    override fun onStart() {
    }
}