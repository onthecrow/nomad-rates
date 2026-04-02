package com.onthecrow.nomadrates.util

expect object ApplicationUtils {
    fun getAppVersion(): String
    fun currentTimeMillis(): Long
}
