package com.rodolfonavalon.canadatransit.util

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.CREATED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.STARTED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.RESUMED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.PAUSED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.STOPPED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.SAVED
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager.LifecycleStage.DESTROYED
import org.robolectric.android.controller.ActivityController
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LifecycleManagerTest {

    @Test
    fun testActivityWatched() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        var expectStage = CREATED
        // Attach the activity to watch
        LifecycleManager.watchActivity(activity) { stage ->
            assertEquals(expectStage, stage)
            false
        }

        expectStage = CREATED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = STARTED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = RESUMED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = PAUSED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = STOPPED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = SAVED
        callControllerActivityLifecycle(expectStage, controller)
        expectStage = DESTROYED
        callControllerActivityLifecycle(expectStage, controller)
    }

    @Test
    fun testActivityMultipleWatched() {
        val controllers = mutableListOf<Pair<LifecycleManager.LifecycleStage, ActivityController<Activity>>>()
        for (i in 1..20) {
            controllers.add(Pair(CREATED, Robolectric.buildActivity(Activity::class.java).create().start()))
        }
        // Attach the activities to watch
        for (i in 0 until controllers.count()) {
            LifecycleManager.watchActivity(controllers[i].second.get()) { stage ->
                assertEquals(controllers[i].first, stage)
                false
            }
        }
        // We do no account for DESTROYED and STOPPED since it will remove by the manager
        val testStages = mutableListOf(
                CREATED,
                STARTED,
                RESUMED,
                PAUSED,
                SAVED
        )
        // Lets test the activity and controller in random.
        fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start
        for (i in 1 until 100) {
            val controllerIndex = (0..controllers.count()).random()
            val stageIndex = (0..(testStages.count())).random()
            controllers[controllerIndex] = Pair(testStages[stageIndex], controllers[controllerIndex].second)
            callControllerActivityLifecycle(testStages[stageIndex], controllers[controllerIndex].second)
        }
    }

    @Test
    fun testActivityWatchAutoRemove() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        var watched = false
        // Attach the activity
        LifecycleManager.watchActivity(activity) { _ ->
            if (!watched) {
                watched = true
            } else {
                fail("The activity failed to be removed")
            }
            // Return true to detach the activity and
            // watch the activity only once
            true
        }
        callAllControllerActivityLifecycle(controller)
    }

    @Test
    fun testActivityIgnored() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        // Attach the activity
        LifecycleManager.watchActivity(activity) { _ ->
            fail("Failed to detach activity")
        }
        // Detach the activity
        LifecycleManager.ignoreActivity(activity)
        // Test that the activity is detached
        callAllControllerActivityLifecycle(controller)
    }

    @Test
    fun testActivityMultipleIgnored() {
        val controllers = mutableListOf<ActivityController<Activity>>()
        // Attach the activities
        for (i in 1..20) {
            val controller = Robolectric.buildActivity(Activity::class.java).create().start()
            controllers.add(controller)
            LifecycleManager.watchActivity(controller.get()) { _ ->
                fail("Failed to detach Activity")
            }
        }
        // Detach the activities
        for (controller in controllers) {
            LifecycleManager.ignoreActivity(controller.get())
        }
        // Test that the activities is detached
        for (controller in controllers) {
            callAllControllerActivityLifecycle(controller)
        }
    }

    @Test
    fun testActivityIgnoreAutoRemove() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        var watched = false
        // Attach the activity
        LifecycleManager.watchActivity(activity) { _ ->
            if (!watched) {
                watched = true
            } else {
                fail("The activity failed to be removed")
            }
            // Return true to detach the activity and
            // watch the activity only once
            true
        }
        // Detach the activity
        LifecycleManager.ignoreActivity(activity)
        // Test that the activity is detached
        callAllControllerActivityLifecycle(controller)
    }

    private fun callControllerActivityLifecycle(stage: LifecycleManager.LifecycleStage, controller: ActivityController<Activity>) {
        when (stage) {
            CREATED -> controller.create()
            STARTED -> controller.start()
            RESUMED -> controller.resume()
            PAUSED -> controller.pause()
            STOPPED -> controller.stop()
            SAVED -> controller.saveInstanceState(Bundle())
            DESTROYED -> controller.destroy()
        }
    }

    private fun callAllControllerActivityLifecycle(controller: ActivityController<Activity>) {
        controller.create()
        controller.start()
        controller.resume()
        controller.pause()
        controller.stop()
        controller.saveInstanceState(Bundle())
        controller.destroy()
    }
}
