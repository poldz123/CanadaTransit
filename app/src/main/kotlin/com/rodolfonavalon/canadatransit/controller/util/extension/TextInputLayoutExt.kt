package com.rodolfonavalon.canadatransit.controller.util.extension

import android.widget.EditText
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import com.rodolfonavalon.canadatransit.controller.CanadaTransitApplication

fun TextInputLayout.showError(message: String) {
    this.error = message
    this.editText?.error = ""
    this.isErrorEnabled = true
}

fun TextInputLayout.showError(@StringRes message: Int) {
    showError(context.getString(message))
}

fun TextInputLayout.hideError() {
    if (!this.isErrorEnabled) { return }
    this.editText?.error = null
    this.isErrorEnabled = false
}

fun TextInputLayout.showHelperText(message: String) {
    this.helperText = message
    this.isHelperTextEnabled = true
    // Make sure that if the last state was an error, set it
    // again to display the error message. This is a bug where
    // the text input will not display error messages when helper
    // text is changed while the error message is currently being display
    // on the screen.
    if (this.isErrorEnabled) {
        this.error = this.error
    }
}

fun TextInputLayout.showHelperText(@StringRes message: Int) {
    showHelperText(context.getString(message))
}

fun TextInputLayout.text(): String? {
    return editText?.text.toString()
}

fun TextInputLayout.setText(text: String) {
    editText?.setText(text)
    editText?.setSelection(text()?.count() ?: 0)
}

fun TextInputLayout.focus() {
    // Prevent focusing the input layout to fix the bug within the
    // robolectric where it is causing collision with fragment manager
    // when something is being commit into it.
    if (CanadaTransitApplication.isEnableJVMTest) { return }
    // Focus the input-layout and then show the keyboard
    isFocusableInTouchMode = true
    requestFocus()
    editText?.apply {
        requestFocus()
        context.showKeyboard(this)
    }
}

fun EditText.focus() {
    // Prevent focusing the input layout to fix the bug within the
    // robolectric where it is causing collision with fragment manager
    // when something is being commit into it.
    if (CanadaTransitApplication.isEnableJVMTest) { return }
    // Focus the input-layout and then show the keyboard
    isFocusableInTouchMode = true
    requestFocus()
    context.showKeyboard(this)
}

fun TextInputLayout.removeFocus() {
    // This should be set to true and after set to false to completely remove the focus on the
    // edit text within the text input layout while the ability to focus will still be intact.
    isFocusableInTouchMode = true
    editText?.apply {
        context.closeKeyboard(this)
        clearFocus()
    }
    isFocusableInTouchMode = false
}
