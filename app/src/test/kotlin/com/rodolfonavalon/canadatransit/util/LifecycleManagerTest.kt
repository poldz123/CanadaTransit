package com.rodolfonavalon.canadatransit.util

import android.app.Activity
import android.os.Bundle
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleCallback
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.CREATED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.DESTROYED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.PAUSED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.RESUMED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.SAVED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.STARTED
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager.LifecycleStage.STOPPED
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LifecycleManagerTest {

    @Test
    fun testWatchActivity_single() {
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
    fun testWatchActivity_multiple() {
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
        fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
        for (i in 1 until controllers.count() * testStages.count()) {
            val controllerIndex = (0..controllers.count()).random()
            val stageIndex = (0..(testStages.count())).random()
            // Since the controller's stage is initialized to CREATED, each test should change its expected
            // stage randomly. But still having the same activity being watched.
            controllers[controllerIndex] = Pair(testStages[stageIndex], controllers[controllerIndex].second)
            callControllerActivityLifecycle(testStages[stageIndex], controllers[controllerIndex].second)
        }
    }

    @Test
    fun testWatchActivity_autoRemoveCallback() {
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
    fun testWatchActivity_preventCallbackDuplication() {
        val controller = Robolectric.buildActivity(Activity::class.java).create().start()
        val activity = controller.get()

        var alreadyCalled = false
        val callback: LifecycleCallback = {
            if (alreadyCalled) {
                fail("Callback is called multiple times")
            } else {
                alreadyCalled = true
                false
            }
        }

        // Attach the activity
        LifecycleManager.watchActivity(activity, callback)
        LifecycleManager.watchActivity(activity, callback)
        // Test that the activity is detached
        callControllerActivityLifecycle(RESUMED, controller)
    }

    @Test
    fun testIgnoreActivity_single() {
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
    fun testIgnoreActivity_multiple() {
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
    fun testIgnoreActivity_afterAutoRemoveCallback() {
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
        // After calling the lifecycle, it should auto removed the
        // callback since it returns true.
        callControllerActivityLifecycle(PAUSED, controller)
        // Detach the activity
        LifecycleManager.ignoreActivity(activity)
        // Test that the activity is detached
        callAllControllerActivityLifecycle(controller)
    }

    private fun callControllerActivityLifecycle(
        stage: LifecycleManager.LifecycleStage,
        controller: ActivityController<Activity>
    ) {
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
