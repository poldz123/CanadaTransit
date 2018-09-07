package com.rodolfonavalon.canadatransit.controller.manager.update

import com.rodolfonavalon.canadatransit.controller.util.queue.Action
import com.rodolfonavalon.canadatransit.controller.util.queue.Task

typealias OnUpdateManagerCompleteListener = () -> Unit

typealias OnSuccessUpdateTaskListener<RESULT> = (RESULT) -> Unit
typealias OnFailureUpdateTaskListener = () -> Unit

interface UpdateTask: Task

interface Updatable: Action {

    /**
     * TODO Update
     */
    fun update()
}
