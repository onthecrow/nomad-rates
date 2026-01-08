package com.onthecrow.nomadrates

import com.onthecrow.nomadrates.currency.di.currencyLogicModule
import com.onthecrow.nomadrates.currency.di.currencyModule
import com.onthecrow.nomadrates.di.applicationModule
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
        applicationModule,
        currencyModule,
        currencyLogicModule,
    )
}
