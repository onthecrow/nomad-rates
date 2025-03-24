package com.onthecrow.nomadrates

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform