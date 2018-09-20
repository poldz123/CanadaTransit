package com.rodolfonavalon.canadatransit.controller.manager.update

import android.content.Intent
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.task.OperatorUpdaterTask
import com.rodolfonavalon.canadatransit.controller.service.UpdateService
import com.rodolfonavalon.canadatransit.controller.service.UpdateService.Companion.ACTION_START_UPDATE_MANAGER
import com.rodolfonavalon.canadatransit.controller.util.extension.uuid
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import io.reactivex.Maybe

class UpdateManager: AbstractQueueTask<UpdateTask>() {

    companion object {
        private val instance: UpdateManager = UpdateManager()

        fun updateOperators(): Maybe<List<Operator>> {
            // Todo - This updates the operator, we do not care if it have a flag
            // that needs to be updated. All operators are updated by default every time.
            val operatorTask = OperatorUpdaterTask(instance)
            instance.add(uuid(), operatorTask)
            start()
            return operatorTask
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

        /**
         * Retrieves the instance of the [QueueTask] which is the instance of the [UpdateManager], this
         * is to encapsulate the manager.
         */
        fun manager(): QueueTask<UpdateTask> {
            return instance
        }

        /**
         * This triggers the manager to start the tasks which only the service can. This is
         * to be able to process the tasks in the background even if the application has
         * already been destroyed.
         */
        private fun start() {
            val context = CanadaTransitApplication.appContext
            val service = Intent(context, UpdateService::class.java)
            service.putExtra(ACTION_START_UPDATE_MANAGER, true)
            context.startService(service)
        }
    }
}
