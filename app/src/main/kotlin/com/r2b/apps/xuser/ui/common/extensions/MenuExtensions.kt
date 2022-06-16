package com.r2b.apps.xuser.ui.common.extensions

import android.content.res.Resources
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.annotation.IdRes

fun Menu.tintItem(resources: Resources, @IdRes menuItemResId: Int, @ColorRes colorResId: Int) {
    val item: MenuItem? = findItem(menuItemResId)
    item?.let { item.icon.setTint(resources.safeGetColor(colorResId)) }
}