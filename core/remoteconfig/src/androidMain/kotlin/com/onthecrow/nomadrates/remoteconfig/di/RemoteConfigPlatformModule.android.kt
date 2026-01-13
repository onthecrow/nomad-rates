package com.onthecrow.nomadrates.remoteconfig.di

import com.onthecrow.nomadrates.remoteconfig.AndroidRemoteConfigProvider
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import org.koin.dsl.bind
import org.koin.dsl.module

actual val remoteConfigPlatformModule = module {
    single { AndroidRemoteConfigProvider() } bind RemoteConfigProvider::class
}
