package com.onthecrow.nomadrates.util

import java.text.DateFormat
import java.util.Date

actual object DateUtils {
    actual fun formatDateTime(timestamp: Long): String {
        return DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
        ).format(Date(toInstant(timestamp).toEpochMilliseconds()))
    }
}
