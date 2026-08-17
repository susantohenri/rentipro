package com.henrisusanto.rentipro.core.util

import java.text.NumberFormat
import java.util.Locale

fun Int.toFormattedString(): String {
    return NumberFormat.getNumberInstance(Locale("id", "ID")).format(this)
}

fun Long.toFormattedString(): String {
    return NumberFormat.getNumberInstance(Locale("id", "ID")).format(this)
}
