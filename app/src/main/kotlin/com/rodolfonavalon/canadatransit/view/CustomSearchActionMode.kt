package com.rodolfonavalon.canadatransit.view

import android.content.Context
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.rodolfonavalon.canadatransit.R
import com.rodolfonavalon.canadatransit.controller.util.extension.activity
import com.rodolfonavalon.canadatransit.controller.util.extension.closeKeyboard
import com.rodolfonavalon.canadatransit.controller.util.extension.focus
import com.rodolfonavalon.canadatransit.controller.util.extension.listenOnImePressed
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class CustomSearchActionMode : ActionMode.Callback {

    private var mode: ActionMode? = null
    private var context: Context? = null

    var isShowing = false

    var onDestroyedListener: (() -> Unit)? = null
    var onQueryListener: ((String) -> Unit)? = null
    private var onCreatedListener: ((mode: ActionMode) -> Unit)? = null

    private var autoCompletePublishSubject = PublishSubject.create<String>()
    private var autoCompleteDisposable: Disposable? = null

    fun start(view: View, createdListener: (mode: ActionMode) -> Unit) {
        this.context = view.context
        this.onCreatedListener = createdListener
        mode?.also { actionMode ->
            // If it is showing already just call the on-created listener all over again to prevent
            // the action mode to show and hide that can cause a bug.
            createdListener(actionMode)
        } ?: view.startActionMode(this)
    }

    fun update() {
        (mode?.customView as EditText).also { editText ->
            onQueryListener?.invoke(editText.text.toString())
        }
    }

    fun finish() {
        mode?.finish()
    }

    fun closeKeyboard() {
        mode?.customView?.also { search ->
            context?.closeKeyboard(search)
        }
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        isShowing = true
        val search = View.inflate(context, R.layout.view_search, null) as EditText
        mode.customView = search
        this.mode = mode
        context?.also { context ->
            context.activity().window?.statusBarColor = ContextCompat.getColor(context, R.color.actionModeColorPrimaryDark)
            // Show the keyboard automatically when this action mode is created. Must be called after
            // this view is referenced to the action mode custom view.
            search.focus()
            search.listenOnImePressed(EditorInfo.IME_ACTION_DONE) {
                closeKeyboard()
            }
            // Create a autocomplete delay to prevent very fast emissions of the searched text
            autoCompleteDisposable = autoCompletePublishSubject
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { text ->
                        onQueryListener?.invoke(text)
                    }
            search.doAfterTextChanged { editable ->
                val text = editable.toString()
                autoCompletePublishSubject.onNext(text)
            }
        }
        onCreatedListener?.invoke(mode)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        isShowing = false
        // Change back the original status bar color
        context?.also { context ->
            context.activity().window?.statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            context.closeKeyboard()
            autoCompleteDisposable?.dispose()
            autoCompleteDisposable = null
            onQueryListener = null
        }
        this.mode = null
        this.context = null
        onDestroyedListener?.invoke()
    }
}
