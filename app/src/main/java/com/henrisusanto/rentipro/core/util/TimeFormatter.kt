package com.henrisusanto.rentipro.core.util

import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormatter {

    fun formatCountdown(millis: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis.coerceAtLeast(0))
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatOverdue(millis: Long): String = "OVERDUE ${formatCountdown(millis)}"
}
