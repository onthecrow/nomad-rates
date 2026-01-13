package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.data.IOSCurrencyRemoteDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

actual val currencyLogicPlatformModule: Module = module {
    single<CurrencyRemoteDataSource> { IOSCurrencyRemoteDataSource() }
}
