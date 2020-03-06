package com.rodolfonavalon.canadatransit.util

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication
import org.robolectric.Shadows

open class BaseRobolectricTest : BaseTest() {

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
