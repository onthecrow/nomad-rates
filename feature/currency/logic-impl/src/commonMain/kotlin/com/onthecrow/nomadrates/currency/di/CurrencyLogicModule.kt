package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyListUseCaseImpl
import org.koin.dsl.module
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import org.koin.core.module.Module

val currencyLogicModule: Module = module {
    includes(currencyLogicPlatformModule)
    single { CurrencyRepository(get()) }
    single<GetCurrencyListUseCase> { GetCurrencyListUseCaseImpl(get()) }
}
