package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.data.AndroidCurrencyRemoteConfigDataSource
import com.onthecrow.nomadrates.currency.data.CurrencyRemoteConfigDataSource
import org.koin.dsl.module

actual val currencyLogicPlatformModule = module {
    single<CurrencyRemoteConfigDataSource> { AndroidCurrencyRemoteConfigDataSource() }
}
