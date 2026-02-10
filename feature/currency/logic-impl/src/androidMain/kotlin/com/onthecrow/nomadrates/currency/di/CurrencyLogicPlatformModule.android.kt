package com.onthecrow.nomadrates.currency.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.onthecrow.nomadrates.currency.data.AndroidCurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import org.koin.dsl.module

actual val currencyLogicPlatformModule = module {
    single<CurrencyRemoteDataSource> {
        val remoteConfig = get<FirebaseRemoteConfig>()
        AndroidCurrencyRemoteDataSource(remoteConfig)
    }
}
