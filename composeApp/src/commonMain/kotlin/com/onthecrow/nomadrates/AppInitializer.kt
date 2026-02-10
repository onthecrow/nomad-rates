package com.onthecrow.nomadrates

import com.onthecrow.nomadrates.di.applicationModule
import org.koin.core.context.startKoin

object AppInitializer {
    fun initialize(platform: Platform) {
        initFirebase(platform)
        initKoin(platform)
    }
}

expect fun initFirebase(platform: Platform)

private fun initKoin(platform: Platform) = startKoin {
    modules(
        platform.platformModule,
        applicationModule,
    )
}
