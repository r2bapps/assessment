package com.r2b.apps.xuser.ui.common.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val defaultDateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun String.localizedDate(pattern: String? = defaultDateFormatPattern): String =
    try {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        dateFormat.parse(this)?.toString() ?: this
    } catch(ex: ParseException) {
        this
    }

fun String.safeLocalizedDate(pattern: String? = defaultDateFormatPattern): String {
    val localized = this.localizedDate(pattern)
    return if (localized == this) "" else localized
}
