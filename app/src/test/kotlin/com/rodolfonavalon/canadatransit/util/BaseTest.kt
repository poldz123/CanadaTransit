package com.rodolfonavalon.canadatransit.util

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

open class BaseTest {

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
}