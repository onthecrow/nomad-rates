package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyListUseCaseImpl
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyUseCaseImpl
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import org.koin.dsl.module
import com.onthecrow.nomadrates.currency.data.CurrencyRepositoryImpl
import org.koin.core.module.Module

val currencyLogicModule: Module = module {
    includes(currencyLogicPlatformModule)
    single<CurrencyRepository> { CurrencyRepositoryImpl(get()) }
    single<GetCurrencyListUseCase> { GetCurrencyListUseCaseImpl(get()) }
    single<GetCurrencyUseCase> { GetCurrencyUseCaseImpl(get()) }
}
