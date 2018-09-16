package com.rodolfonavalon.canadatransit.controller.manager.update

import com.rodolfonavalon.canadatransit.controller.util.queue.Action
import com.rodolfonavalon.canadatransit.controller.util.queue.Task

typealias OnSuccessTaskListener<T> = (T) -> Unit
typealias OnFailureTaskListener = () -> Unit

typealias OnFinishManagerListener = () -> Unit
typealias OnStartManagerListener = () -> Unit

interface UpdateTask: Task

interface Updatable: Action {

    /**
     * TODO Update
     */
    fun update()
}
