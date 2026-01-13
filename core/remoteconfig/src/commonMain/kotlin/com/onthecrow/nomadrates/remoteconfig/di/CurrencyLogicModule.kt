package com.onthecrow.nomadrates.remoteconfig.di

import org.koin.core.module.Module
import org.koin.dsl.module

val remoteConfigModule: Module = module {
    includes(remoteConfigPlatformModule)
}
