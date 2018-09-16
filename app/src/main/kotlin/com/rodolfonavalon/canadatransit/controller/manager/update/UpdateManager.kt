package com.rodolfonavalon.canadatransit.controller.manager.update

import android.content.Intent
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.controller.manager.update.task.OperatorUpdaterTask
import com.rodolfonavalon.canadatransit.controller.service.UpdateService
import com.rodolfonavalon.canadatransit.controller.service.UpdateService.Companion.ACTION_START_UPDATE_MANAGER
import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueTask
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

    private var onFinishManagerListener: OnFinishManagerListener? = null
    private var onStartManagerListener: OnStartManagerListener? = null

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onStart() {
        onStartManagerListener?.invoke()
    }

    override fun onFinish() {
        onFinishManagerListener?.invoke()
    }

    fun addStartListener(callback: OnStartManagerListener) {

    }

    fun addFinishListener(callback: OnFinishManagerListener) {

    }

    companion object {
    private val instance: UpdateManager = UpdateManager()

        fun updateOperators(success: OnSuccessTaskListener<List<Operator>>? = null,
                            failure: OnFailureTaskListener? = null): String {
            // Todo - This updates the operator, we do not care if it have a flag
            // that needs to be updated. All operators are updated by default every time.
            val trackingId = generateTrackingId()
            instance.add(trackingId, OperatorUpdaterTask(instance, success, failure))
            startService()
            return trackingId
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
        fun startTasks(OnFinishManagerListener: OnFinishManagerListener? = null) {
            instance.onFinishManagerListener = OnFinishManagerListener
            instance.start()
        }

        fun stopTasks() {
            // TODO stop the tasks which means cancel all task
        }

        private fun startService() {
            val context = CanadaTransitApplication.appContext
            val service = Intent(context, UpdateService::class.java)
            service.putExtra(ACTION_START_UPDATE_MANAGER, true)
            context.startService(service)
        }

        private fun generateTrackingId(): String {
            return UUID.randomUUID().toString()
        }
    }
}
