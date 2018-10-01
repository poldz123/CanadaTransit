package com.rodolfonavalon.canadatransit.controller.manager.update

import android.content.Intent
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.task.UpdateOperatorFeedTask
import com.rodolfonavalon.canadatransit.controller.manager.update.task.UpdateOperatorFeedVersionTask
import com.rodolfonavalon.canadatransit.controller.manager.update.task.UpdateOperatorTask
import com.rodolfonavalon.canadatransit.controller.service.UpdateService
import com.rodolfonavalon.canadatransit.controller.service.UpdateService.Companion.ACTION_START_UPDATE_MANAGER
import com.rodolfonavalon.canadatransit.controller.util.extension.uuid
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueManager
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueManager
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import io.reactivex.Single

class UpdateManager : AbstractQueueManager<UpdateTask>() {

    override fun onStartService() {
        val context = CanadaTransitApplication.appContext
        val service = Intent(context, UpdateService::class.java)
        service.putExtra(ACTION_START_UPDATE_MANAGER, true)
        context.startService(service)
    }

    companion object {
        private val instance: UpdateManager = UpdateManager()

        fun updateOperators(): Single<List<Operator>> {
            return instance.add(uuid(), UpdateOperatorTask())
        }

        fun updateOperatorFeeds(): Single<List<OperatorFeed>> {
            return instance.add(uuid(), UpdateOperatorFeedTask())
        }

        fun updateOperatorFeed(operator: Operator) {
            // Todo - This adds the operator feeds to the pool of operator feeds that needs to be updated.
            // and updates it within the database
        }

        fun updateOperatorFeedVersions(): Single<List<OperatorFeedVersion>> {
            return instance.add(uuid(), UpdateOperatorFeedVersionTask())
        }

        fun updateOperatorFeedVersion(operatorFeed: OperatorFeed) {
            // Todo - This adds the operator feeds version to the pool of operator feeds that needs to be updated.
            // and updates it within the database
        }

        fun updateEverything() {
            // Todo - This updates everything, from operator, operator feeds, operator feed version.
        }

        fun update() {
            updateOperators()
            updateOperatorFeeds()
            updateOperatorFeedVersions()
        }

        /**
         * Retrieves the instance of the [QueueManager] which is the instance of the [UpdateManager], this
         * is to encapsulate the manager.
         */
        fun manager(): QueueManager<UpdateTask> {
            return instance
        }
    }
}
