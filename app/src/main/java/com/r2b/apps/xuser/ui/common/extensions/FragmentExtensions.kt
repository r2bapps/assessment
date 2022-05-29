package com.r2b.apps.xuser.ui.common.extensions

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.ui.common.FragmentViewBindingDelegate

inline fun <reified T : ViewBinding> Fragment.viewBinding() =
    FragmentViewBindingDelegate(T::class.java, this)

fun Fragment.navigate(action: NavDirections) =
    findNavController().navigate(action)

fun Fragment.showError(error: Throwable) =
    this.view?.prettyToast(context?.let { error.localizedMessage(it) } ?: String.format(getString(R.string.unknown_error), error.message))

fun Fragment.showConfirm(confirmMessage: String, onConfirmAction: () -> Unit, onDismissAction: (() -> Unit)?) =
    this.view?.prettyConfirmToast(confirmMessage, onConfirmAction, onDismissAction)

fun Fragment.showConfirm(@StringRes resId: Int, onConfirmAction: () -> Unit, onDismissAction: (() -> Unit)?) =
    showConfirm(getString(resId), onConfirmAction, onDismissAction)

fun Fragment.prettyToast(message: String) =
    this.view?.prettyToast(message)

