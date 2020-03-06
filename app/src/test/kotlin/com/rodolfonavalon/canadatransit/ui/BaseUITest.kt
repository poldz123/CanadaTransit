package com.rodolfonavalon.canadatransit.ui

import android.app.Activity
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import com.rodolfonavalon.canadatransit.util.rule.SynchronousTestRule
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.robolectric.Shadows

open class BaseUITest {

    @get:Rule
    val synchronousTask = SynchronousTestRule()
    @get:Rule
    val instantTask = InstantTaskExecutorRule()

    lateinit var compositeDisposable: CompositeDisposable

    @Before
    open fun setup() {
        compositeDisposable = CompositeDisposable()
    }

    @After
    open fun teardown() {
    }

    inline fun <reified T : Activity> launchActivity(
        intent: Intent = Intent(CanadaTransitApplication.appContext, T::class.java),
        vararg permissions: String,
        crossinline callback: (T) -> Unit
    ): ActivityScenario<T> {
        return ActivityScenario.launch<T>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val application = Shadows.shadowOf(activity.application)
                permissions.forEach { permission ->
                    application.grantPermissions(permission)
                }
                callback(activity)
            }
        }
    }

    inline fun <reified T : Activity> launchActivity(
        vararg permissions: String,
        crossinline callback: (T) -> Unit
    ): ActivityScenario<T> {
        return ActivityScenario.launch(T::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val application = Shadows.shadowOf(activity.application)
                permissions.forEach { permission ->
                    application.grantPermissions(permission)
                }
                callback(activity)
            }
        }
    }

    inline fun <reified T : Activity> launch(): ActivityScenario<T> {
        return ActivityScenario.launch(T::class.java)
    }
}
