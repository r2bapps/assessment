package com.r2b.apps.xuser.ui.common.extensions

import android.os.SystemClock
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.r2b.apps.xuser.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.prettyToast(message: String) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()

fun View.prettyConfirmToast(confirmMessage: String, onConfirmAction: () -> Unit, onDismissAction: (() -> Unit)?) =
    Snackbar
        .make(this, confirmMessage, Snackbar.LENGTH_SHORT)
        .setAction(R.string.yes) { onConfirmAction() }
        .addCallback(object: Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == DISMISS_EVENT_TIMEOUT) {
                    onDismissAction?.let { it.invoke() }
                }
            }
        })
        .show()


