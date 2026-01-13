package com.onthecrow.nomadrates.remoteconfig.di

import com.onthecrow.nomadrates.remoteconfig.IOSRemoteConfigProvider
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import org.koin.dsl.bind
import org.koin.dsl.module

actual val remoteConfigPlatformModule = module {
    single { IOSRemoteConfigProvider() } bind RemoteConfigProvider::class
}
