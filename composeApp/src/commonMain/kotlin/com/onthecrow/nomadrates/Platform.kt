package com.onthecrow.nomadrates

import com.onthecrow.nomadrates.currency.di.currencyModule
import org.koin.core.context.startKoin

interface Platform {
    val name: String
    fun initialize() {
        initFirebase()
        initKoin()
    }
}

expect fun getPlatform(): Platform

expect fun initFirebase()

private fun initKoin() = startKoin {
    modules(
        currencyModule,
    )
}
