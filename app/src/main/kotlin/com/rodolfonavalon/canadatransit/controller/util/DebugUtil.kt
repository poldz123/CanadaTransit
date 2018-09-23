package com.rodolfonavalon.canadatransit.controller.util

import android.os.Looper

/**
 * DebugUtil
 */
object DebugUtil {

    private var isDisabledBackgroundCheck = false

    /**
     * assertFalse
     */
    fun assertFalse(assertion: Boolean, message: String? = null) {
        assert(!assertion, message)
    }

    /**
     * assertTrue
     */
    fun assertTrue(assertion: Boolean, message: String? = null) {
        assert(assertion, message)
    }

    /**
     * assertNull
     */
    fun <T> assertNull(assertion: T, message: String? = null) {
        assert(assertion == null, message)
    }

    /**
     * assertNull
     */
    fun <T> assertNotNull(assertion: T, message: String? = null) {
        assert(assertion != null, message)
    }

    /**
     * assertEqual
     */
    fun <F, S> assertEqual(first: F, second: S, message: String? = null) {
        assert(first === second, message)
    }

    /**
     * assertMainThread
     */
    fun assertMainThread() {
        if (!isDisabledBackgroundCheck) {
            assertTrue(Looper.myLooper() == Looper.getMainLooper(),
                "Method should be executed within the Main Thread!!")
        }
    }

    /**
     * assertWorkerThread
     */
    fun assertWorkerThread() {
        if (!isDisabledBackgroundCheck) {
            assertFalse(Looper.myLooper() == Looper.getMainLooper(),
                    "Method should be executed within the Worker Thread!!")
        }
    }

    /**
     * This is to disable the main and background threads assertion. This is
     * used by the JVM unit tests to prevent assertion since the unit tests
     * always run in the main thread.
     */
    fun disableBackgroundChecks() {
        isDisabledBackgroundCheck = true
    }

    private fun assert(value: Boolean, message: String? = null) {
        if (!value) {
            // TODO Crashlytics
            throw AssertionError(message ?: "Assertion Failed")
        }
    }
}
