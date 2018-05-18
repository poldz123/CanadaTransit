package com.rodolfonavalon.canadatransit.controller.util

import android.os.Looper

/**
 * DebugUtil
 */
object DebugUtil {

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
     * assertEqual
     */
    fun assertEqual(first: Any, second: Any, message: String? = null) {
        assert(first === second, message)
    }

    /**
     * assertMainThread
     */
    fun assertMainThread() {
        assertTrue(Looper.myLooper() == Looper.getMainLooper(),
                "Method should be executed within the Main Thread!!")
    }

    /**
     * assertWorkerThread
     */
    fun assertWorkerThread() {
        assertFalse(Looper.myLooper() == Looper.getMainLooper(),
                "Method should be executed within the Worker Thread!!")
    }

    private fun assert(value: Boolean, message: String? = null) {
        if (!value) {
            // TODO: Crashlytics
            throw AssertionError(message ?: "Assertion Failed")
        }
    }

}