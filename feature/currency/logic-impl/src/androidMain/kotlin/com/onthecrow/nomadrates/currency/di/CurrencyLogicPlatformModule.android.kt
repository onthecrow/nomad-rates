package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.data.AndroidCurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import org.koin.dsl.module

actual val currencyLogicPlatformModule = module {
    single<CurrencyRemoteDataSource> { AndroidCurrencyRemoteDataSource() }
}
