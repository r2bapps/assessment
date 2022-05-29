package com.r2b.apps.xuser.ui.common.extensions

import android.content.Context
import com.r2b.apps.xuser.R
import okhttp3.internal.http2.ConnectionShutdownException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun Throwable.localizedMessage(context: Context): String? =
    when(this) {
        is SocketTimeoutException -> String.format(
            context.getString(R.string.SocketTimeoutException),
            context.getString(R.string.please_check_your_internet_connection),
        )
        is UnknownHostException -> String.format(
            context.getString(R.string.UnknownHostException),
            context.getString(R.string.please_check_your_internet_connection),
        )
        is ConnectionShutdownException -> String.format(
            context.getString(R.string.ConnectionShutdownException),
            context.getString(R.string.please_check_your_internet_connection),
        )
        is IOException -> context.getString(R.string.IOException)
        is HttpException -> context.getString(R.string.HttpException)
        is IllegalStateException -> context.getString(R.string.IllegalStateException)
        else -> {
            localizedMessage
        }
    }

