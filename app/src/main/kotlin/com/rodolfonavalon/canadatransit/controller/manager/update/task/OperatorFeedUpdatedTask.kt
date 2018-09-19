package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.manager.update.util.AbstractUpdateTask
import io.reactivex.Observable

class OperatorFeedUpdatedTask(updateManager: UpdateManager):
        AbstractUpdateTask(updateManager) {
}
