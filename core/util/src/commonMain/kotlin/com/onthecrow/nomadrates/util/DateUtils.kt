package com.onthecrow.nomadrates.util

import kotlin.time.Instant

expect object DateUtils {
    fun formatDateTime(timestamp: Long): String
}

fun normalizeEpochMillis(timestamp: Long): Long {
    return if (timestamp < 10_000_000_000L) timestamp * 1_000 else timestamp
}

fun toInstant(timestamp: Long): Instant {
    return Instant.fromEpochMilliseconds(normalizeEpochMillis(timestamp))
}
