package com.onthecrow.nomadrates.util

actual object ApplicationUtils {
    actual fun getAppVersion(): String = ""

    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}
