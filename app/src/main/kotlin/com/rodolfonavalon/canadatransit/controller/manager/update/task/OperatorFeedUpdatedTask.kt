package com.rodolfonavalon.canadatransit.controller.manager.update.task

import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager
import com.rodolfonavalon.canadatransit.controller.util.queue.task.AbstractObservableTask
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed

class OperatorFeedUpdatedTask(updateManager: UpdateManager) : AbstractObservableTask<List<OperatorFeed>>()
