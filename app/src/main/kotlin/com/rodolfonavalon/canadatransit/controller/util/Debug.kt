package com.rodolfonavalon.canadatransit.controller.util

import android.os.Looper

object Debug {

    fun assertFalse(assertion: Boolean) {
        assert(!assertion)
    }

    fun assertFalse(assertion: Boolean, lazyMessage: () -> Any) {
        assert(!assertion, lazyMessage)
    }

    fun assertTrue(assertion: Boolean) {
        assert(assertion)
    }

    fun assertTrue(assertion: Boolean, lazyMessage: () -> Any) {
        assert(assertion, lazyMessage)
    }

    fun assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val message = "Method should be executed within the Main Thread!!"
            throw AssertionError(message)
        }
    }

    fun assertWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            val message = "Method should be executed within the Worker Thread!!"
            throw AssertionError(message)
        }
    }

    private fun assert(value: Boolean) {
        assert(value) { "Assertion Failed" }
    }

    private inline fun assert(value: Boolean, lazyMessage: () -> Any) {
        if (!value) {
            val message = lazyMessage()
            throw AssertionError(message)
        }
    }

}