package com.r2b.apps.xuser.ui.common.extensions

import android.content.res.Resources
import android.os.Build
import androidx.annotation.ColorRes

fun Resources.safeGetColor(@ColorRes id: Int): Int =
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(id, null)
    } else {
        getColor(id)
    }