package com.rodolfonavalon.canadatransit.controller.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.util.Pair
import java.util.*

/**
 * Callback method when an activity has triggered its life cycle.
 *
 * @return boolean
 * True when callback will be removed from the pool
 */
typealias LifecycleCallback = (LifecycleManager.LifecycleStage) -> Boolean

/**
 * This manages the life cycle of the activities which handles the callbacks and the
 * pool of activities. Once the activity is registered it will call its life cycle callback
 * multiple times, until, either it is unregistered or when the activity is destroyed.
 */
class LifecycleManager : Application.ActivityLifecycleCallbacks {

    private val lifecycleItems = mutableListOf<LifecycleItem>()

    /**
     * Life cycle item of the manager, which take the activity and callback when lifecycle triggers.
     */
    private data class LifecycleItem(val activity: Activity, val callbacks: MutableList<LifecycleCallback>)

    /**
     * Life cycle stages of the activity represented in enum values.
     */
    enum class LifecycleStage {
        CREATED, // onActivityCreated(Activity, Bundle)
        STARTED, // onActivityStarted(Activity)
        RESUMED, // onActivityResumed(Activity)
        PAUSED, // onActivityPaused(Activity)
        STOPPED, // onActivityStopped(Activity)
        SAVED, // onActivitySaveInstanceState(Activity, Bundle)
        DESTROYED   // onActivityDestroyed(Activity)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle) {
        signalActivity(activity, LifecycleStage.CREATED)
    }

    override fun onActivityStarted(activity: Activity) {
        signalActivity(activity, LifecycleStage.STARTED)
    }

    override fun onActivityResumed(activity: Activity) {
        signalActivity(activity, LifecycleStage.RESUMED)
    }

    override fun onActivityPaused(activity: Activity) {
        signalActivity(activity, LifecycleStage.PAUSED)
    }

    override fun onActivityStopped(activity: Activity) {
        signalActivity(activity, LifecycleStage.STOPPED)
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        signalActivity(activity, LifecycleStage.SAVED)
    }

    override fun onActivityDestroyed(activity: Activity) {
        signalActivity(activity, LifecycleStage.DESTROYED)
        ignoreActivity(activity)
    }

    companion object {
        private val instance: LifecycleManager = LifecycleManager()

        /**
         * Watches the activity and registers the life cycle that can trigger every [LifecycleStage].
         *
         * ATTENTION:  This wont remove the activity cycle callback until either the activity
         * is destroyed or it is unregistered through [.ignoreActivity].
         *
         * @param activity
         * The activity to listen for life cycle callbacks
         * @param callback
         * The callback method whenever the activity triggered the life cycle callbacks
         */
        fun watchActivity(activity: Activity, callback: LifecycleCallback) {
            // If the activity already exist before then just attach the callback
            for ((itemActivity, itemCallbacks) in instance.lifecycleItems) {
                if (itemActivity === activity) {
                    itemCallbacks.add(callback)
                    return
                }
            }
            // Register the activity and then attach it with the callback
            activity.application.registerActivityLifecycleCallbacks(instance)
            instance.lifecycleItems.add(LifecycleItem(
                    activity = activity,
                    callbacks = mutableListOf(callback))
            )
        }

        /**
         * Ignores the activity and unregister it from all life cycle callbacks.
         *
         * ATTENTION:  This must be called whenever an activity is registered for callbacks
         * through [.watchActivity]
         *
         * @param activity
         * The activity to unregister the life cycle callbacks
         */
        fun ignoreActivity(activity: Activity) {
            activity.application.unregisterActivityLifecycleCallbacks(instance)
            val iterator = instance.lifecycleItems.listIterator()
            while (iterator.hasNext()) {
                val (itemActivity, itemCallbacks) = iterator.next()
                if (itemActivity === activity) {
                    itemCallbacks.clear()
                    iterator.remove()
                    return
                }
            }
        }

        /**
         * Signals the activity for a life cycle callback which will trigger the [LifecycleCallback]
         * if ever it exist in the pool.
         *
         * @param activity
         * The activity to signal for life cycle callbacks
         * @param stage
         * The stage of the activity life cycle
         */
        private fun signalActivity(activity: Activity, stage: LifecycleStage) {
            for ((itemActivity, itemCallbacks) in instance.lifecycleItems) {
                if (itemActivity === activity) {
                    val iterator = itemCallbacks.listIterator()
                    while (iterator.hasNext()) {
                        val callback = iterator.next()
                        // Only remove the callback when the callee returns true
                        if (callback.invoke(stage)) {
                            iterator.remove()
                        }
                    }
                    // If there are no more callbacks remove the activity from the pool
                    if (itemCallbacks.isEmpty()) {
                        ignoreActivity(itemActivity)
                    }
                    return
                }
            }
        }
    }
}

