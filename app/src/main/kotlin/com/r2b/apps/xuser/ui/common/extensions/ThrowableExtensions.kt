package com.r2b.apps.xuser.ui.common.extensions

import android.content.Context
import com.r2b.apps.xuser.R
import java.io.IOException
import java.net.SocketTimeoutException


fun Throwable.localizedMessage(context: Context): String? =
    when(this) {
        is SocketTimeoutException -> context.getString(R.string.socket_timeout_error)
        is IOException -> String.format(
            context.getString(R.string.internet_connection_error),
            this.message,
        )
        else -> {
            String.format(context.getString(R.string.unknown_error_template), this.message)
        }
    }

