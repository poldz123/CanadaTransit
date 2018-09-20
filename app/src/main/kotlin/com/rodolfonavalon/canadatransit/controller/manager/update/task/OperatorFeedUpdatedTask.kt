package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.util.AbstractUpdateTask
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed

class OperatorFeedUpdatedTask(updateManager: UpdateManager):
        AbstractUpdateTask<List<OperatorFeed>>(updateManager) {
}
