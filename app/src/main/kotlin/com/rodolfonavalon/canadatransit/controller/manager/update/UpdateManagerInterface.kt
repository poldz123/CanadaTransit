package com.rodolfonavalon.canadatransit.controller.manager.update

import com.rodolfonavalon.canadatransit.controller.util.queue.action.Action
import com.rodolfonavalon.canadatransit.controller.util.queue.task.Task

interface UpdateTask : Task

interface Updatable : Action {

    /**
     * TODO Update
     */
    fun update()
}
