package com.rodolfonavalon.canadatransit.controller.util.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.activity(): Activity {
    if (this is ContextWrapper) {
        if (this is Activity || this.baseContext is Activity) {
            return this as Activity
        } else {
            error("Context does not inherit from Activity.")
        }
    } else {
        error("Context does not inherit from Activity.")
    }
}

fun Context.showKeyboard(view: View) {
    // Prevent opening the keyboard to fix the bug within the
    // robolectric where it is causing collision with fragment manager
    // when something is being commit into it.
    if (CanadaTransitApplication.isEnableJVMTest) { return }
    // Show the keyboard
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Context.closeKeyboard(view: View) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.closeKeyboard() {
    this.activity().currentFocus?.apply {
        val imm = this@closeKeyboard.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun Context.clearFocus() {
    closeKeyboard()
    activity().window.decorView.clearFocus()
}
