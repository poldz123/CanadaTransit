package com.rodolfonavalon.canadatransit.controller.manager.update

import com.rodolfonavalon.canadatransit.controller.manager.update.task.OperatorUpdaterTask
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import java.util.*

class UpdateManager: AbstractQueueTask<UpdateTask>() {

    private enum class UpdateCommand {
        OPERATOR,
        OPERATOR_FEED,
        OPERATOR_FEED_VERSION,
        EVERYTHING
    }

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onComplete() {

    }

    companion object {
        private val instance: UpdateManager = UpdateManager()

        fun updateOperators() {
            // Todo - This updates the operator, we do not care if it have a flag
            // that needs to be updated. All operators are updated by default every time.
            val trackingId = generateTrackingId()
            instance.add(trackingId, OperatorUpdaterTask(instance))
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

        private fun generateTrackingId(): String {
            return UUID.randomUUID().toString()
        }
    }
}