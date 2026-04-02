package com.onthecrow.nomadrates.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSTimeIntervalSince1970

actual object DateUtils {
    actual fun formatDateTime(timestamp: Long): String {
        val formatter = NSDateFormatter().apply {
            dateStyle = NSDateFormatterMediumStyle
            timeStyle = NSDateFormatterShortStyle
        }

        return formatter.stringFromDate(
            NSDate(
                timeIntervalSinceReferenceDate =
                    toInstant(timestamp).toEpochMilliseconds().toDouble() / 1_000.0 - NSTimeIntervalSince1970
            )
        )
    }
}
