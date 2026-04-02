package com.onthecrow.nomadrates.util

import platform.Foundation.NSBundle
import platform.Foundation.NSDate
import platform.Foundation.NSTimeIntervalSince1970

actual object ApplicationUtils {
    actual fun getAppVersion(): String {
        return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            ?: ""
    }

    actual fun currentTimeMillis(): Long {
        return ((NSDate().timeIntervalSinceReferenceDate + NSTimeIntervalSince1970) * 1_000).toLong()
    }
}
