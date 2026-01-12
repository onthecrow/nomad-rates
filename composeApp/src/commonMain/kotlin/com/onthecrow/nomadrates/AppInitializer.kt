package com.onthecrow.nomadrates

import com.onthecrow.nomadrates.di.applicationModule
import org.koin.core.context.startKoin

object AppInitializer {
    fun initialize(platform: Platform) {
        initFirebase()
        initKoin(platform)
    }
}

expect fun initFirebase()

private fun initKoin(platform: Platform) = startKoin {
    modules(
        platform.platformModule,
        applicationModule,
    )
}
