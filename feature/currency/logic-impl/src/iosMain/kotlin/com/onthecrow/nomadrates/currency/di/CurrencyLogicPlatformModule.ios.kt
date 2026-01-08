package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.data.CurrencyRemoteConfigDataSource
import com.onthecrow.nomadrates.currency.data.IOSCurrencyRemoteConfigDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

actual val currencyLogicPlatformModule: Module = module {
    single<CurrencyRemoteConfigDataSource> { IOSCurrencyRemoteConfigDataSource() }
}
